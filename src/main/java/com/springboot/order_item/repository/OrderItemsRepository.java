package com.springboot.order_item.repository;

import com.springboot.order_item.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemsRepository extends JpaRepository <OrderItems, Long> {

}
