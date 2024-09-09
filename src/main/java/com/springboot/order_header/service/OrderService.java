package com.springboot.order_header.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.repository.OrderHeadersRepository;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderHeadersRepository orderHeadersRepository;
    private final OrderItemsRepository orderItemsRepository;

    public OrderService(OrderHeadersRepository orderHeadersRepository, OrderItemsRepository orderItemsRepository) {
        this.orderHeadersRepository = orderHeadersRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    public OrderHeaders createOrder(OrderHeaders orderHeaders) {
        return orderHeadersRepository.save(orderHeaders);
    }

    public OrderHeaders updateOrder(OrderHeaders orderHeaders) {
        // order 상태 변경, 납기일

        OrderHeaders findOrder = findVerifiedOrder(orderHeaders.getOrderId());

        Optional.ofNullable(orderHeaders.getOrderStatus()).ifPresent(findOrder::setOrderStatus);
        Optional.ofNullable(orderHeaders.getRequestDate()).ifPresent(findOrder::setRequestDate);


        //승인. 반려 상태로는 변경 못하게 해야하고
        //승인 이후에는 주문 취소를 못해야한다.

        return orderHeadersRepository.save(findOrder);
    }

    public OrderItems updateOrderItem(Long orderId, Long itemId, OrderItems orderItems) {
        //item 수정 - 수량, 금액
        OrderHeaders orderHeaders = findVerifiedOrder(orderId);
        OrderItems findItem = findVerifiedOrderItems(itemId);

        Optional.ofNullable(orderItems.getQuantity()).ifPresent(findItem::setQuantity);
        Optional.ofNullable(orderItems.getUnitPrice()).ifPresent(findItem::setUnitPrice);
        Optional.ofNullable(orderItems.getStartDate()).ifPresent(findItem::setStartDate);
        Optional.ofNullable(orderItems.getEndDate()).ifPresent(findItem::setEndDate);

        return orderItemsRepository.save(findItem);
    }

    public OrderHeaders updateStatus(Long orderId, OrderHeaders.OrderStatus status) {
        OrderHeaders orderHeaders = findVerifiedOrder(orderId);
        orderHeaders.setOrderStatus(status);
        return orderHeadersRepository.save(orderHeaders);
    }

    public OrderHeaders findOrder(Long orderId) {
        return findVerifiedOrder(orderId);
    }

    public Page<OrderHeaders> findOrders (int page, int size, String status, LocalDate startDate, LocalDate endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        LocalDateTime searchStartDate = startDate.atStartOfDay();
        LocalDateTime searchEndDate = endDate.atTime(23, 59, 59);

        if(status.equalsIgnoreCase("ALL")) {
            return orderHeadersRepository.findByRequestDateBetween(searchStartDate, searchEndDate,pageable);
        } else {
            OrderHeaders.OrderStatus orderStatus = OrderHeaders.OrderStatus.valueOf(status.toUpperCase());
            return orderHeadersRepository.findByRequestDateBetweenAndOrderStatus(searchStartDate, searchEndDate, orderStatus, pageable);
        }
    }

    public OrderHeaders findVerifiedOrder(Long orderId) {
        Optional<OrderHeaders> findOrder = orderHeadersRepository.findById(orderId);
        return findOrder.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND));
    }

    private OrderItems findVerifiedOrderItems(Long itemId) {
        Optional<OrderItems> findItem = orderItemsRepository.findById(itemId);
        return findItem.orElseThrow(() -> new BusinessLogicException(ExceptionCode.ITEM_NOT_FOUND));
    }

}
