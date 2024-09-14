package com.springboot.item.mapper;

import com.springboot.item.dto.Dto;
import com.springboot.item.entity.Item;
import com.springboot.report.reportDto.ReportDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item itemPostDtoToItem(Dto.ItemPostDto postDto);
    List<Item> itemPostDtosToItems(List<Dto.ItemPostDto> postDtos);
    Item itemPatchToItem(Dto.ItemPatchDto patchDto);
    List<Item> itemPatchDtosToItems(List<Dto.ItemPatchDto> patchDtos);
    default Dto.ItemResponseDto itemToResponseDto(Item item , ReportDto.InventoryDto report) {
        Dto.ItemResponseDto itemResponseDto = new Dto.ItemResponseDto();

        itemResponseDto.setItemId( item.getItemId() );
        itemResponseDto.setItemCd( item.getItemCd() );
        itemResponseDto.setItemNm( item.getItemNm() );
        itemResponseDto.setCategory( item.getCategory() );
        itemResponseDto.setUnit( item.getUnit() );
        itemResponseDto.setUnitPrice( item.getUnitPrice() );
        itemResponseDto.setColor( item.getColor() );
        itemResponseDto.setSize( item.getSize() );
        itemResponseDto.setItemStatus( item.getItemStatus() );
        itemResponseDto.setCreatedAt( item.getCreatedAt() );
        itemResponseDto.setTotalStock(report.getTotalStock());
        itemResponseDto.setPrepareOrder(report.getPreparedOrder());
        itemResponseDto.setUnusedStock(report.getUnusedStock());
        return itemResponseDto;
    }
    List<Dto.ItemResponseDto> itemToResponseDtos(List<Item> items);
}
