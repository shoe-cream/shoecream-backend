package com.springboot.item.mapper;

import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemPostDtoToItem(Dto.ItemPostDto postDto);
    List<Item> itemPostDtosToItems(List<Dto.ItemPostDto> postDtos);
    Item itemPatchToItem(Dto.ItemPatchDto patchDto);
    List<Item> itemPatchDtosToItems(List<Dto.ItemPatchDto> patchDtos);
    Dto.ItemResponseDto itemToResponseDto(Item item);
    List<Dto.ItemResponseDto> itemToResponseDtos(List<Item> items);
}
