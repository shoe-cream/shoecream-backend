package com.springboot.order_header.controller;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.mapper.OrderMapper;
import com.springboot.order_header.service.OrderService;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import com.springboot.sale_history.repository.SaleHistoryRepository;
import com.springboot.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final MemberService memberService;
    private final BuyerService buyerService;
    private final SaleHistoryRepository saleHistoryRepository;

    public OrderController(OrderService orderService, OrderMapper orderMapper, MemberService memberService, BuyerService buyerService, SaleHistoryRepository saleHistoryRepository) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.memberService = memberService;
        this.buyerService = buyerService;
        this.saleHistoryRepository = saleHistoryRepository;
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
        saleHistoryRepository.save(orderMapper.orderToSaleHistory(createOrder));

        URI location = UriCreator.createUri(ORDER_DEFAULT_URL, createOrder.getOrderId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{order-id}")
    public ResponseEntity patchOrder(@Positive @PathVariable("order-id") Long orderId,
                                     @Valid @RequestBody OrderDto.OrderPatch orderPatchDto /*, Authentication authentication*/) {
        orderPatchDto.setOrderId(orderId);
        OrderHeaders orderHeaders = orderService.updateOrder(orderMapper.orderPatchDtoToOrder(orderPatchDto));
        saleHistoryRepository.save(orderMapper.orderToSaleHistory(orderHeaders));

        return new ResponseEntity(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(orderHeaders)), HttpStatus.OK);
    }

    @PatchMapping("/{order-id}/items/{item-id}")
    public ResponseEntity patchOrderItem(@Positive @PathVariable("order-id") Long orderId,
                                         @Positive @PathVariable("item-id") Long itemId,
                                     @Valid @RequestBody OrderDto.ItemPatch itemPatch /*, Authentication authentication*/) {

        OrderItems item = orderService.updateOrderItem(orderId, itemId, orderMapper.itemPatchDtoToOrderItem(itemPatch));
        OrderHeaders orderHeaders = orderService.findVerifiedOrder(orderId);
        item.setOrderHeaders(orderHeaders);

        saleHistoryRepository.save(orderMapper.orderToSaleHistory(orderHeaders));
        return new ResponseEntity(orderMapper.orderToOrderResponseDto(orderHeaders), HttpStatus.OK);
    }

    //팀장 승인
    @PatchMapping("/{order-id}/approve")
    public ResponseEntity approveStatus(@Positive @PathVariable("order-id") Long orderId) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.APPROVED);
        saleHistoryRepository.save(orderMapper.orderToSaleHistory(updatedOrder));
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(updatedOrder)),HttpStatus.OK);
    }

    //팀장 반려
    @PatchMapping("/{order-id}/reject")
    public ResponseEntity rejectStatus(@Positive @PathVariable("order-id") Long orderId) {
        //팀장권한확인
        OrderHeaders updatedOrder = orderService.updateStatus(orderId, OrderHeaders.OrderStatus.REJECTED);

        saleHistoryRepository.save(orderMapper.orderToSaleHistory(updatedOrder));
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(updatedOrder)),HttpStatus.OK);
    }

    //개별조회
    @GetMapping("/{order-id}")
    public ResponseEntity getOrder(@Positive @PathVariable("order-id") Long orderId) {
        OrderHeaders orderHeaders = orderService.findOrder(orderId);
        return new ResponseEntity<>(new SingleResponseDto<>(orderMapper.orderToOrderResponseDto(orderHeaders)),HttpStatus.OK);
    }

    //status, 기간 으로 filtering
    @GetMapping
    public ResponseEntity getOrders(@RequestParam(required = false) String status,
                                    @RequestParam(required = false, defaultValue = "19000101") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate searchStartDate,
                                    @RequestParam(required = false, defaultValue = "99991231") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate searchEndDate,
                                    @Positive @RequestParam int page,
                                    @Positive @RequestParam int size) {


        Page<OrderHeaders> orderPages = orderService.findOrders(page-1, size, status, searchStartDate, searchEndDate);
        List<OrderHeaders> orderLists = orderPages.getContent();
        return new ResponseEntity(new MultiResponseDto<>(orderMapper
                .ordersToOrderResponseDtos(orderLists), orderPages),HttpStatus.OK);
    }
}
