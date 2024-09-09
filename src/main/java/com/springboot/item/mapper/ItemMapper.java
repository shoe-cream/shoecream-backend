package com.springboot.item.mapper;

import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemPostDtoToItem(Dto.ItemPostDto postDto);
}
