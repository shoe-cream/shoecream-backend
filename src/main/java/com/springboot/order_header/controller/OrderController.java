package com.springboot.order_header.controller;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.mapper.OrderMapper;
import com.springboot.order_header.service.OrderService;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final static String ORDER_DEFAULT_URL = "/orders";
    private final MemberService memberService;
    private final BuyerService buyerService;

    public OrderController(OrderService orderService, OrderMapper orderMapper, MemberService memberService, BuyerService buyerService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.memberService = memberService;
        this.buyerService = buyerService;
    }

    @PostMapping
    public ResponseEntity postOrder(@Valid @RequestBody OrderDto.Post orderPostDto/*, Authentication authentication*/) {
        // authentication 받아서 처리
        // Member member = memberService.findVerifiedEmployee();

        OrderHeaders orderHeaders = orderMapper.orderPostDtoToOrder(orderPostDto);
        Buyer buyer = buyerService.findVerifiedBuyer(orderPostDto.getBuyerCD());
        //orderHeaders.setMember(member);
        orderHeaders.setBuyer(buyer);

        List<OrderItems> orderItemsList = orderMapper.orderItemDtosToOrderItems(orderPostDto.getOrderItems());
        for (OrderItems orderItem : orderItemsList) {
            orderItem.setOrderHeaders(orderHeaders);
        }
        orderHeaders.setOrderItems(orderItemsList);

        OrderHeaders createOrder = orderService.createOrder(orderHeaders);

        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, createOrder.getOrderId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{order-id}")
    public ResponseEntity patchOrder(@Positive @PathVariable("order-id") Long orderId,
                                     @Valid @RequestBody OrderDto.OrderPatch orderPatchDto /*, Authentication authentication*/) {
        orderPatchDto.setOrderId(orderId);
        OrderHeaders orderHeaders = orderService.updateOrder(orderMapper.orderPatchDtoToOrder(orderPatchDto));
        return new ResponseEntity(orderMapper.orderToOrderResponseDto(orderHeaders), HttpStatus.OK);
    }

    @PatchMapping("/{order-id}/items/{item-id}")
    public ResponseEntity patchOrderItem(@Positive @PathVariable("order-id") Long orderId,
                                         @Positive @PathVariable("item-id") Long itemId,
                                     @Valid @RequestBody OrderDto.ItemPatch itemPatch /*, Authentication authentication*/) {
        OrderItems item = orderService.updateOrderItem(orderId, itemId, orderMapper.itemPatchDtoToOrderItem(itemPatch));
        OrderHeaders orderHeaders = orderService.findVerifiedOrder(orderId);
        item.setOrderHeaders(orderHeaders);
        return new ResponseEntity(orderMapper.orderToOrderResponseDto(orderHeaders), HttpStatus.OK);
    }

    //팀장 승인
    @PatchMapping("/{order-id}/approve")
    public ResponseEntity approveStatus(@Positive @PathVariable("order-id") Long orderId) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.APPROVED);
        return new ResponseEntity<>(orderMapper.orderToOrderResponseDto(updatedOrder),HttpStatus.OK);
    }

    //팀장 반려
    @PatchMapping("/{order-id}/reject")
    public ResponseEntity rejectStatus(@Positive @PathVariable("order-id") Long orderId) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.REJECTED);
        return new ResponseEntity<>(orderMapper.orderToOrderResponseDto(updatedOrder),HttpStatus.OK);
    }

//    @GetMapping("/{order-id}")
//    public ResponseEntity getOrder(@PathVariable Long orderId) {
//        OrderHeaders orderHeaders = orderService.findOrder(orderId);
//        return new ResponseEntity<>(orderMapper.orderToOrderResponseDto(),HttpStatus.OK);
//    }
}
