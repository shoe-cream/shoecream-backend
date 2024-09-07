package com.springboot.order_header.mapper;

import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import org.hibernate.criterion.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
   OrderHeaders orderPostDtoToOrder(OrderDto.Post orderPostDto);
   OrderHeaders orderPatchDtoToOrder(OrderDto.Patch orderPatchDto);
   OrderDto.Response orderToOrderResponseDto(OrderHeaders order);
   List<OrderItems> orderItemDtosToOrderItems(List<OrderDto.OrderItemDto> orderItemDtos);
}
