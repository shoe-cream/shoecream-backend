package com.springboot.order_header.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.repository.OrderHeadersRepository;
import com.springboot.order_header.repository.OrderQueryRepositoryCustom;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemsRepository;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.sale_history.mapper.SaleHistoryMapper;
import com.springboot.sale_history.repository.SaleHistoryRepository;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderHeadersRepository orderHeadersRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final OrderQueryRepositoryCustom orderQueryRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final SaleHistoryMapper saleHistoryMapper;
    private final MemberService memberService;

    public OrderService(OrderHeadersRepository orderHeadersRepository, OrderItemsRepository orderItemsRepository, OrderQueryRepositoryCustom orderQueryRepository, SaleHistoryRepository saleHistoryRepository, SaleHistoryMapper saleHistoryMapper, MemberService memberService) {
        this.orderHeadersRepository = orderHeadersRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.orderQueryRepository = orderQueryRepository;
        this.saleHistoryRepository = saleHistoryRepository;
        this.saleHistoryMapper = saleHistoryMapper;
        this.memberService = memberService;
    }

    // 주문 등록
    public OrderHeaders createOrder(OrderHeaders orderHeaders, Authentication authentication) {
        Member member = verifiedMember(authentication);
        orderHeaders.setMember(member);
        OrderHeaders orderHeader =  orderHeadersRepository.save(orderHeaders);
        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeader, member));
        return orderHeader;
    }

    // OrderHeader 수정 : 주문에 대한 상태랑 납기일 변경
    public OrderHeaders updateOrder(OrderHeaders orderHeaders, Authentication authentication) {

        Member member = verifiedMember(authentication);
        OrderHeaders findOrder = findVerifiedOrder(orderHeaders.getOrderId());
        //승인. 반려 상태로는 변경 못한다. (팀장만 가능 , updateStatus 메서드로 승인
        if(orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.APPROVED || orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.REJECTED) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        //승인 이후에는 주문취소로 변경할 수 없다.
        //즉 findOrder 상태가 견적요청, 발주요청 상태가 아닐 경우에는 취소할 수없다.
        List<OrderHeaders.OrderStatus> notApproveStatus =
                Arrays.asList(OrderHeaders.OrderStatus.PURCHASE_REQUEST, OrderHeaders.OrderStatus.REQUEST_TEMP);

        if(!notApproveStatus.contains(findOrder.getOrderStatus()) && orderHeaders.getOrderStatus() == OrderHeaders.OrderStatus.CANCELLED) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_CHANGE_ORDER_STATUS);
        }

        Optional.ofNullable(orderHeaders.getOrderStatus()).ifPresent(findOrder::setOrderStatus);
        Optional.ofNullable(orderHeaders.getRequestDate()).ifPresent(findOrder::setRequestDate);

        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(findOrder, member));
        return orderHeadersRepository.save(findOrder);
    }

    //item 수정 - 수량, 금액
    public OrderItems updateOrderItem(Long orderId, Long itemId, OrderItems orderItems, Authentication authentication) {
        Member member = verifiedMember(authentication);
        OrderHeaders orderHeaders = findVerifiedOrder(orderId);
        OrderItems findItem = findVerifiedOrderItems(itemId);

        Optional.ofNullable(orderItems.getQuantity()).ifPresent(findItem::setQuantity);
        Optional.ofNullable(orderItems.getUnitPrice()).ifPresent(findItem::setUnitPrice);
        Optional.ofNullable(orderItems.getStartDate()).ifPresent(findItem::setStartDate);
        Optional.ofNullable(orderItems.getEndDate()).ifPresent(findItem::setEndDate);

        findItem.setOrderHeaders(orderHeaders);
        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeaders, member));
        return orderItemsRepository.save(findItem);
    }

    //팀장이 승인, 반려 버튼을 눌렀을 때
    public OrderHeaders updateStatus(Long orderId, OrderHeaders.OrderStatus status, Authentication authentication) {
        Member member = verifiedMember(authentication);
        OrderHeaders orderHeaders = findVerifiedOrder(orderId);
        orderHeaders.setOrderStatus(status);

        saleHistoryRepository.save(saleHistoryMapper.orderToSaleHistory(orderHeaders, member));
        return orderHeadersRepository.save(orderHeaders);
    }

    public OrderHeaders findOrder(Long orderId) {
        return findVerifiedOrder(orderId);
    }

    public Page<OrderHeaders> findOrders(int page, int size, OrderDto.OrderSearchRequest orderSearchRequest) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderQueryRepository.findByRequestDateBetweenAndOrderStatusAndBuyer_BuyerCdAndOrderItems_ItemCD(orderSearchRequest, pageable);
    }

    public Page<SaleHistory> findHistories (int page, int size, Long orderId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return saleHistoryRepository.findByOrderHeaders_OrderId(orderId, pageable);
    }

    public OrderHeaders findVerifiedOrder(Long orderId) {
        Optional<OrderHeaders> findOrder = orderHeadersRepository.findById(orderId);
        return findOrder.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }

    private OrderItems findVerifiedOrderItems(Long itemId) {
        Optional<OrderItems> findItem = orderItemsRepository.findById(itemId);
        return findItem.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

    private Member verifiedMember(Authentication authentication) {

        String user = (String) authentication.getPrincipal();
        return memberService.findVerifiedMember(user);
    }
}
