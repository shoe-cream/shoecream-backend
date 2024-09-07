package com.springboot.order_item.repository;

import com.springboot.order_item.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository <OrderItems, Long> {
}
