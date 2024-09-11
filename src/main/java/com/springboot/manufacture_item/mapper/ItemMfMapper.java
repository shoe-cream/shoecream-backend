package com.springboot.manufacture_item.mapper;

import com.springboot.item.entity.Item;
import com.springboot.manufacture_item.dto.Dto;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture.entity.Manufacture;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMfMapper {
    default ItemManufacture itemMfPostDtoToItemMf(Dto.ItemMfPostDto postDto) {
        Item item = new Item();
        item.setItemId(postDto.getItemId());
        Manufacture manufacture = new Manufacture();
        manufacture.setMfId(postDto.getMfId());

        ItemManufacture itemManufacture = new ItemManufacture();
        itemManufacture.setItem(item);
        itemManufacture.setManufacture(manufacture);
        itemManufacture.setUnitPrice(postDto.getUnitPrice());
        itemManufacture.setQty(postDto.getQty());

        return itemManufacture;
    }

    List<ItemManufacture> postDtosToItemManufactures(List<Dto.ItemMfPostDto> postDtos);

    default Dto.ItemMfResponseDto itemMfToItemMfResponseDto(ItemManufacture itemManufacture) {
        Dto.ItemMfResponseDto responseDto = new Dto.ItemMfResponseDto();

        responseDto.setItemCd(itemManufacture.getItem().getItemCd());
        responseDto.setItemNm(itemManufacture.getItem().getItemNm());
        responseDto.setRegion(itemManufacture.getManufacture().getRegion());
        responseDto.setMfNm(itemManufacture.getManufacture().getMfNm());
        responseDto.setMfCd(itemManufacture.getManufacture().getMfCd());
        responseDto.setEmail(itemManufacture.getManufacture().getEmail());
        responseDto.setUnitPrice(itemManufacture.getUnitPrice());
        responseDto.setQty(itemManufacture.getQty());

        return responseDto;
    }

    default List<Dto.ItemMfResponseDto> itemMfToItemMfResponseDtos(List<ItemManufacture> itemManufactures) {
        return itemManufactures
                .stream()
                .map(itemManufacture -> Dto.ItemMfResponseDto
                        .builder()
                        .region(itemManufacture.getManufacture().getRegion())
                        .email(itemManufacture.getManufacture().getEmail())
                        .mfCd(itemManufacture.getManufacture().getMfCd())
                        .mfNm(itemManufacture.getManufacture().getMfNm())
                        .itemCd(itemManufacture.getItem().getItemCd())
                        .itemNm(itemManufacture.getItem().getItemNm())
                        .unitPrice(itemManufacture.getUnitPrice())
                        .qty(itemManufacture.getQty())
                        .build())
                .collect(Collectors.toList());
    }

    default ItemManufacture itemMfPatchDtoToItemMf(Dto.ItemMfPatchDto patchDto) {
        ItemManufacture itemManufacture = new ItemManufacture();
        itemManufacture.setMfItemId(patchDto.getMfItemId());
        itemManufacture.setUnitPrice(patchDto.getUnitPrice());
        itemManufacture.setQty(patchDto.getQty());

        return itemManufacture;
    }
}
