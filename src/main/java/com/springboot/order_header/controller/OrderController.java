package com.springboot.order_header.controller;

import com.querydsl.core.types.Order;
import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.report.reportDto.ReportDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.mapper.OrderMapper;
import com.springboot.order_header.service.OrderService;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.sale_history.mapper.SaleHistoryMapper;
import com.springboot.utils.UriCreator;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final BuyerService buyerService;
    private final SaleHistoryMapper saleHistoryMapper;
    private final static String ORDER_DEFAULT_URI ="/orders";

    public OrderController(OrderService orderService, OrderMapper orderMapper, BuyerService buyerService, SaleHistoryMapper saleHistoryMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.buyerService = buyerService;
        this.saleHistoryMapper = saleHistoryMapper;
    }

    // 주문 등록
    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody List<OrderDto.Post> orderPostDtos, Authentication authentication) {

        List<OrderHeaders> orders = new ArrayList<>();

        for(OrderDto.Post post : orderPostDtos) {
            OrderHeaders orderHeaders = orderMapper.orderPostDtoToOrder(post);

            //buyer 저장
            Buyer buyer = buyerService.findVerifiedBuyer(post.getBuyerCd());
            orderHeaders.setBuyer(buyer);

            //item 저장
            List<OrderItems> orderItemsList = orderMapper.orderItemDtosToOrderItems(post.getOrderItems());
            for (OrderItems orderItem : orderItemsList) {
                if(orderItem.getStartDate().isAfter(orderHeaders.getRequestDate()) || (orderItem.getEndDate().isBefore(orderHeaders.getRequestDate()))){
                    throw new BusinessLogicException(ExceptionCode.CHECK_CONTRACT_DATE);
                }

                if(orderItem.getQty() < 0) {
                    throw new BusinessLogicException(ExceptionCode.CANNOT_ORDER_NEGATIVE_QUANTITY);
                }
                orderItem.setOrderHeaders(orderHeaders);
            }

            orderHeaders.setOrderItems(orderItemsList);
            OrderHeaders order = orderService.createOrder(orderHeaders, authentication);
            orders.add(order);

        }

        return new ResponseEntity(orderMapper.ordersToOrderResponseDtos(orders), HttpStatus.CREATED);
    }

    //주문 (order-header) 수정
    @PatchMapping
    public ResponseEntity patchOrder(@Valid @RequestBody List<OrderDto.OrderPatch> orderPatchDtos , Authentication authentication) {
        List<OrderHeaders> orderHeaderList = new ArrayList<>();
        for(OrderDto.OrderPatch orderPatchDto : orderPatchDtos) {
            OrderHeaders orderHeaders = orderService.updateOrder(orderMapper.orderPatchDtoToOrder(orderPatchDto), authentication);
            orderHeaderList.add(orderHeaders);
        }
        return new ResponseEntity(new SingleResponseDto<>(orderMapper.ordersToOrderResponseDtos(orderHeaderList)), HttpStatus.OK);
    }

    //주문 (order-item) 수정
    @PatchMapping("/items")
    public ResponseEntity patchOrderItem(@Valid @RequestBody List<OrderDto.ItemPatch> itemPatches , Authentication authentication) {

        List<OrderHeaders> orderHeaderList = new ArrayList<>();

        for(OrderDto.ItemPatch itemPatch : itemPatches) {
            orderService.updateOrderItem(itemPatch.getOrderId(), itemPatch.getItemId(), orderMapper.itemPatchDtoToOrderItem(itemPatch), authentication);
            OrderHeaders orderHeaders = orderService.findVerifiedOrder(itemPatch.getOrderId());
            orderHeaderList.add(orderHeaders);
        }

        return new ResponseEntity(orderMapper.ordersToOrderResponseDtos(orderHeaderList), HttpStatus.OK);
    }

    //주문 - 팀장 승인
    @PatchMapping("/approve")
    public ResponseEntity approveStatus(@Valid @RequestBody List<OrderDto.ApprovalOrRejectDto> approvalDtos,
                                        Authentication authentication) {

        List<OrderHeaders> orderHeaders = new ArrayList<>();

        for(OrderDto.ApprovalOrRejectDto approvalDto : approvalDtos) {
            OrderHeaders updatedOrder = orderService
                    .updateStatus(approvalDto.getOrderCd(),
                            OrderHeaders.OrderStatus.APPROVED,
                            approvalDto.getRejectReason(),
                            authentication);

            orderHeaders.add(updatedOrder);
        }
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.ordersToOrderResponseDtos(orderHeaders)),HttpStatus.OK);
    }

    //주문 - 팀장 반려
    @PatchMapping("/reject")
    public ResponseEntity rejectStatus(@Valid @RequestBody List<OrderDto.ApprovalOrRejectDto> rejectDtos,
                                       Authentication authentication) {

        List<OrderHeaders> orderHeaders = new ArrayList<>();

        for(OrderDto.ApprovalOrRejectDto rejectDto : rejectDtos){
            OrderHeaders updatedOrder = orderService
                    .updateStatus(rejectDto.getOrderCd(),
                            OrderHeaders.OrderStatus.REJECTED,
                            rejectDto.getRejectReason(), authentication);

            orderHeaders.add(updatedOrder);
        }
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.ordersToOrderResponseDtos(orderHeaders)),HttpStatus.OK);
    }

    //주문 개별 조회
    @GetMapping("/{order-cd}")
    public ResponseEntity getOrder(@PathVariable("order-cd") String orderCd) {
        OrderHeaders orderHeaders = orderService.findVerifiedOrderByCd(orderCd);
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(orderHeaders)),HttpStatus.OK);
    }


    //조회조건 : 주문 상태별, buyerCode별, itemCode별, 날짜별로 조회가능(기본값 별도)
    @GetMapping
    public ResponseEntity getOrders(
            @RequestParam(required = false) String buyerCd,
            @RequestParam(required = false) String itemCd,
            @RequestParam(required = false) OrderHeaders.OrderStatus status,
            @RequestParam(required = false) String orderCd,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchStartDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchEndDate,
            @Positive @RequestParam int page,
            @Positive @RequestParam int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        String sortCriteria = "orderId";
        if(sort != null) {
            List<String> sorts = Arrays.asList("orderId", "orderCd", "requestDate", "createdAt", "orderStatus");
            if (sorts.contains(sort)) {
                sortCriteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        OrderDto.OrderSearchRequest orderSearchRequest = new OrderDto.OrderSearchRequest(buyerCd, itemCd, status, orderCd, searchStartDate, searchEndDate);

        Page<OrderHeaders> orderPages = orderService.findOrders(page - 1, size, sortCriteria, direction, orderSearchRequest);
        List<OrderHeaders> orderLists = orderPages.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(orderMapper.ordersToOrderResponseDtos(orderLists), orderPages), HttpStatus.OK);
    }

    //SaleHistory 조회
    @GetMapping("/{order-cd}/histories")
    public ResponseEntity getOrderHistory(@PathVariable("order-cd") String orderCd,
                                          @Positive @RequestParam int page,
                                          @Positive @RequestParam int size,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) String direction) {
        //정렬기준
        String sortCriteria = "createdAt";
        if(sort != null) {
            List<String> sorts = Arrays.asList("createdAt", "saleHistoryId", "personInCharge", "buyerCd", "requestDate", "orderDate", "orderStatus");
            if (sorts.contains(sort)) {
                sortCriteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<SaleHistory> historyPages = orderService.findHistories(page - 1, size, sortCriteria, direction, orderCd);
        List<SaleHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(saleHistoryMapper.saleHistoriesToSaleHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }

    //판매 report
    @GetMapping("/reports")
    public ResponseEntity totalTopSales (@RequestParam (required = false) Integer topNumber,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        if(startDate == null) {
            startDate= LocalDate.of(1900, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.of(9999, 12, 31);
        }

        List<ReportDto.SaleReportDto> dtos = new ArrayList<>();

        // top Number 가 없으면 주문량으로 정렬한 전체 조회
        if (topNumber == null) {
            dtos = orderService.generateReport(startDate, endDate);
        } else {
            //top Number 있으면 주문금액 순으로 (topNumber) 수만큼 조회
            dtos = orderService.generateTopReport(startDate, endDate, topNumber);
        }

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    //재고 조회
    @GetMapping("/inventories")
    public ResponseEntity getStock (@RequestParam String itemCd) {
        ReportDto.InventoryDto dto = orderService.getStock(itemCd);

        return new ResponseEntity(dto, HttpStatus.OK);
    }

    //사원별 판매실적 조회
    @GetMapping("/employees")
    public ResponseEntity getEmployeeReport (@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        if(startDate == null) {
            startDate= LocalDate.of(1900, 1, 1);
        }
        if (endDate == null) {
            endDate = LocalDate.of(9999, 12, 31);
        }

        return new ResponseEntity(orderService.getEmployeeReport(startDate, endDate), HttpStatus.OK);
    }
}
