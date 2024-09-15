package com.springboot.order_header.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.report.reportDto.ReportDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.repository.OrderHeadersRepository;
import com.springboot.order_header.repository.OrderQueryRepositoryCustom;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemsRepository;
import com.springboot.report.service.SaleReport;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.sale_history.mapper.SaleHistoryMapper;
import com.springboot.sale_history.repository.SaleHistoryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class OrderService {
    private final OrderHeadersRepository orderHeadersRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final OrderQueryRepositoryCustom orderQueryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final SaleHistoryMapper saleHistoryMapper;
    private final MemberService memberService;
    private final SaleReport saleReport;

    public OrderService(OrderHeadersRepository orderHeadersRepository,
                        OrderItemsRepository orderItemsRepository,
                        OrderQueryRepositoryCustom orderQueryRepository,
                        SaleHistoryRepository saleHistoryRepository,
                        SaleHistoryMapper saleHistoryMapper,
                        MemberService memberService,
                        SaleReport saleReport) {

        this.orderHeadersRepository = orderHeadersRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.orderQueryRepository = orderQueryRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.saleHistoryMapper = saleHistoryMapper;
        this.memberService = memberService;
        this.saleReport = saleReport;
    }

    @Transactional
    public OrderHeaders createOrder(OrderHeaders orderHeaders, Authentication authentication) {
        //담당자 설정
        Member member = verifiedMember(authentication);
        orderHeaders.setMember(member);

        // 재고량이 없을 때 예외처리
        isStock(orderHeaders);

        // 주문 코드 생성 후 설정
        String orderCd = createOrderCd();
        orderHeaders.setOrderCd(orderCd);

        // DB에 저장
        OrderHeaders orderHeader = orderHeadersRepository.save(orderHeaders);
        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeader, member));
        return orderHeader;
    }

    // OrderHeader 수정 : 주문에 대한 상태랑 납기일 변경
    @Transactional
    public OrderHeaders updateOrder(OrderHeaders orderHeaders, Authentication authentication) {

        Member member = verifiedMember(authentication);
        OrderHeaders findOrder = findVerifiedOrder(orderHeaders.getOrderId());

        //승인. 반려 상태로는 변경 못한다. (팀장만 가능 , updateStatus 메서드로 승인)
        if (orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.APPROVED || orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.REJECTED) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        //승인 이후에는 주문 취소로 변경할 수 없다.
        List<OrderHeaders.OrderStatus> notApproveStatus =
                Arrays.asList(OrderHeaders.OrderStatus.PURCHASE_REQUEST, OrderHeaders.OrderStatus.REQUEST_TEMP);

        if (!notApproveStatus.contains(findOrder.getOrderStatus()) && orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.CANCELLED) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER_STATUS);
        }

        //취소상태에서 주문상태를 변경할 수 없다
        if (findOrder.getOrderStatus() == OrderHeaders.OrderStatus.CANCELLED) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER_STATUS);
        }

        // 상태, 납기일 변경
        Optional.ofNullable(orderHeaders.getOrderStatus()).ifPresent(findOrder::setOrderStatus);
        Optional.ofNullable(orderHeaders.getRequestDate()).ifPresent(findOrder::setRequestDate);

        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(findOrder, member));
        return orderHeadersRepository.save(findOrder);
    }

    //item 수정 - 수량, 금액
    @Transactional
    public OrderItems updateOrderItem(Long orderId, Long itemId, OrderItems orderItems, Authentication authentication) {
        Member member = verifiedMember(authentication);
        OrderHeaders orderHeaders = findVerifiedOrder(orderId);
        OrderItems findItem = findVerifiedOrderItems(itemId);

        //주문내역에 해당 item 이 있는지 확인
        if (!findItem.getOrderHeaders().getOrderId().equals(orderId)) {
            throw new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND_IN_ORDER);
        }

        //팀장 승인 이후에는 아이템의 상태를 변경할 수 없다.
        if(!orderHeaders.getOrderStatus().equals(OrderHeaders.OrderStatus.PURCHASE_REQUEST) &&
        !orderHeaders.getOrderStatus().equals(OrderHeaders.OrderStatus.REQUEST_TEMP)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER_STATUS);
        }

        Optional.ofNullable(orderItems.getQty()).ifPresent(qty -> {
            findItem.setQty(qty);
            isStock(findItem.getOrderHeaders());
        });

        Optional.ofNullable(orderItems.getUnitPrice()).ifPresent(findItem::setUnitPrice);
        Optional.ofNullable(orderItems.getStartDate()).ifPresent(findItem::setStartDate);
        Optional.ofNullable(orderItems.getEndDate()).ifPresent(findItem::setEndDate);

        findItem.setOrderHeaders(orderHeaders);
        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeaders, member));

        return orderItemsRepository.save(findItem);
    }

    //팀장이 승인, 반려 버튼을 눌렀을 때
    @Transactional
    public OrderHeaders updateStatus(String orderCd, OrderHeaders.OrderStatus status, String reason, Authentication authentication) {
        Member member = verifiedMember(authentication);
        OrderHeaders orderHeaders = findVerifiedOrderByCd(orderCd);

        //승인 시 재고 확인
        if (status.equals(OrderHeaders.OrderStatus.APPROVED)) {
            isStock(orderHeaders);

        } else {
            //반려시 사유 확인
            orderHeaders.setRejectReason(reason);
        }

        orderHeaders.setOrderStatus(status);

        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeaders, member));
        return orderHeadersRepository.save(orderHeaders);
    }

    // order 조회 (조회조건 (조합 가능) : 주문 상태별, buyerCode별, itemCode별, 날짜별로 조회가능(기본값 별도))
    public Page<OrderHeaders> findOrders(int page, int size, String criteria, String direction, OrderDto.OrderSearchRequest orderSearchRequest) {
        Pageable pageable = createPageable(page, size, criteria, direction);
        return orderQueryRepository.findByCreatedAtBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCdAndOrderCd(orderSearchRequest, pageable);
    }

    private Pageable createPageable(int page, int size, String sortCriteria, String direction) {
        Sort.Direction sortDirection = (direction == null || direction.isEmpty()) ? Sort.Direction.DESC : Sort.Direction.fromString(direction);
        Sort sort = Sort.by(sortDirection, sortCriteria);

        return PageRequest.of(page, size, sort);
    }

    // 판매내역 조회 (order-code 로 분류)
    public Page<SaleHistory> findHistories(int page, int size, String criteria, String direction, String orderCd) {
        Pageable pageable = createPageable(page, size, criteria, direction);
        return saleHistoryRepository.findByOrderCd(orderCd, pageable);
    }

    //orderId 검증
    public OrderHeaders findVerifiedOrder(Long orderId) {
        Optional<OrderHeaders> findOrder = orderHeadersRepository.findById(orderId);

        return findOrder.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }

    //orderCd 검증
    public OrderHeaders findVerifiedOrderByCd(String orderCd) {
        Optional<OrderHeaders> findOrder = orderHeadersRepository.findByOrderCd(orderCd);

        return findOrder.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_CD_NOT_FOUND));
    }

    // orderItem 검증 및 조회
    private OrderItems findVerifiedOrderItems(Long itemId) {
        Optional<OrderItems> findItem = orderItemsRepository.findById(itemId);

        return findItem.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    //member 검증 및 반환
    private Member verifiedMember(Authentication authentication) {
        String user = (String) authentication.getPrincipal();

        return memberService.findVerifiedEmployee(user);
    }

    // 판매 report (마진률)
    public List<ReportDto.SaleReportDto> generateReport(LocalDate startDate, LocalDate endDate) {

        return saleReport.getSaleReport(startDate, endDate);
    }

    // 아이템 재고 조회
    public ReportDto.InventoryDto getStock(String itemCd) {

        return saleReport.getInventory(itemCd);
    }

    //재고 여부 확인
    private void isStock (OrderHeaders orderHeaders) {
        boolean isStock = orderHeaders.getOrderItems()
                .stream()
                .map(orderItems -> saleReport.calculateInventory(orderItems.getItemCd()) - orderItems.getQty())
                .anyMatch(qty -> qty < 0);
        if (isStock) {
            throw new BusinessLogicException(ExceptionCode.OUT_OF_STOCK);
        }
    }

    // 주문 코드 생성 메서드
    private String createOrderCd() {
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        return "SHO" + uuid;
    }
}
