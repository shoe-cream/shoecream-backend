package com.springboot.order_header.service;

import com.springboot.buyer.entity.Buyer;
import com.springboot.buyer.service.BuyerService;
import com.springboot.member.service.MemberService;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_header.repository.OrderHeadersRepository;
import com.springboot.order_item.repository.OrderItemsRepository;
import org.springframework.stereotype.Service;

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

        return orderHeadersRepository.save(orderHeaders);
    }

//    public OrderHeaders findOrder(Long orderId) {
//
//    }



}
