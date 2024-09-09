package com.springboot.order_header.controller;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.mapper.OrderMapper;
import com.springboot.order_header.service.OrderService;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.sale_history.entity.SaleHistory;
import com.springboot.sale_history.mapper.SaleHistoryMapper;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final static String ORDER_DEFAULT_URL = "/orders";
    private final BuyerService buyerService;
    private final SaleHistoryMapper saleHistoryMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper, BuyerService buyerService, SaleHistoryMapper saleHistoryMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.buyerService = buyerService;
        this.saleHistoryMapper = saleHistoryMapper;
    }

    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody OrderDto.Post orderPostDto, Authentication authentication) {

        OrderHeaders orderHeaders = orderMapper.orderPostDtoToOrder(orderPostDto);
        Buyer buyer = buyerService.findVerifiedBuyer(orderPostDto.getBuyerCD());
        orderHeaders.setBuyer(buyer);

        List<OrderItems> orderItemsList = orderMapper.orderItemDtosToOrderItems(orderPostDto.getOrderItems());
        for (OrderItems orderItem : orderItemsList) {
            orderItem.setOrderHeaders(orderHeaders);
        }
        orderHeaders.setOrderItems(orderItemsList);

        OrderHeaders createOrder = orderService.createOrder(orderHeaders, authentication);

        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, createOrder.getOrderId());
        return ResponseEntity.created(location).build();
    }

    //order-header 수정
    @PatchMapping("/{order-id}")
    public ResponseEntity patchOrder(@Positive @PathVariable("order-id") Long orderId,
                                     @Valid @RequestBody OrderDto.OrderPatch orderPatchDto , Authentication authentication) {
        orderPatchDto.setOrderId(orderId);
        OrderHeaders orderHeaders = orderService.updateOrder(orderMapper.orderPatchDtoToOrder(orderPatchDto), authentication);

        return new ResponseEntity(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(orderHeaders)), HttpStatus.OK);
    }

    //order-item 수정
    @PatchMapping("/{order-id}/items/{item-id}")
    public ResponseEntity patchOrderItem(@Positive @PathVariable("order-id") Long orderId,
                                         @Positive @PathVariable("item-id") Long itemId,
                                     @Valid @RequestBody OrderDto.ItemPatch itemPatch , Authentication authentication) {

        OrderItems item = orderService.updateOrderItem(orderId, itemId, orderMapper.itemPatchDtoToOrderItem(itemPatch), authentication);
        OrderHeaders orderHeaders = orderService.findVerifiedOrder(orderId);

        return new ResponseEntity(orderMapper.orderToOrderResponseDto(orderHeaders), HttpStatus.OK);
    }

    //팀장 승인
    @PatchMapping("/{order-id}/approve")
    public ResponseEntity approveStatus(@Positive @PathVariable("order-id") Long orderId, Authentication authentication) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.APPROVED, authentication);
    //    saleHistoryRepository.save(orderMapper.orderToSaleHistory(updatedOrder));
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(updatedOrder)),HttpStatus.OK);
    }

    //팀장 반려
    @PatchMapping("/{order-id}/reject")
    public ResponseEntity rejectStatus(@Positive @PathVariable("order-id") Long orderId, Authentication authentication) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.REJECTED, authentication);

        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(updatedOrder)),HttpStatus.OK);
    }

    //주문 개별 조회
    @GetMapping("/{order-id}")
    public ResponseEntity getOrder(@Positive @PathVariable("order-id") Long orderId) {
        OrderHeaders orderHeaders = orderService.findOrder(orderId);
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(orderHeaders)),HttpStatus.OK);
    }


    //조회조건 : 주문 상태별, buyerCode별, itemCode별, 날짜별로 조회가능(기본값 별도)
    @GetMapping
    public ResponseEntity getOrders(
            @RequestParam(required = false) String buyerCode,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) OrderHeaders.OrderStatus status,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchStartDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate searchEndDate,
            @Positive @RequestParam int page,
            @Positive @RequestParam int size) {

        OrderDto.OrderSearchRequest orderSearchRequest = new OrderDto.OrderSearchRequest(buyerCode, itemCode, status, orderId, searchStartDate, searchEndDate);
        Page<OrderHeaders> orderPages = orderService.findOrders(page - 1, size, orderSearchRequest);
        List<OrderHeaders> orderLists = orderPages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(orderMapper.ordersToOrderResponseDtos(orderLists), orderPages), HttpStatus.OK);
    }

    //SaleHistory 조회
    @GetMapping("{order-id}/history")
    public ResponseEntity getOrderHistory(@Positive @PathVariable("order-id") Long orderId,
                                          @Positive @RequestParam int page,
                                          @Positive @RequestParam int size) {
        Page<SaleHistory> historyPages = orderService.findHistories(page - 1, size, orderId);
        List<SaleHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(saleHistoryMapper.saleHistoriesToSaleHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }
}
