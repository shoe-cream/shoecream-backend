package com.springboot.order_header.mapper;

import com.springboot.order_header.dto.OrderDto;
import com.springboot.order_header.entity.OrderHeaders;
import com.springboot.order_item.entity.OrderItems;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
   OrderHeaders orderPostDtoToOrder(OrderDto.Post orderPostDto);
   OrderHeaders orderPatchDtoToOrder(OrderDto.OrderPatch orderPatchDto);
   OrderItems itemPatchDtoToOrderItem(OrderDto.ItemPatch itemPatchDto);

   default OrderDto.Response orderToOrderResponseDto(OrderHeaders order) {
      OrderDto.Response.ResponseBuilder response = OrderDto.Response.builder();
      response.orderId(order.getOrderId());
      response.buyerCD(order.getBuyer().getBuyerCd());
      response.buyerNm(order.getBuyer().getBuyerNm());
      response.createdAt(order.getCreatedAt());
      response.requestDate(order.getRequestDate());
      response.status(order.getOrderStatus());
      response.orderItems(order.getOrderItems());
      return response.build();
   }

   List<OrderItems> orderItemDtosToOrderItems(List<OrderDto.OrderItemDto> orderItemDtos);

   List<OrderDto.Response> ordersToOrderResponseDtos (List<OrderHeaders> orderHeaders);
}
