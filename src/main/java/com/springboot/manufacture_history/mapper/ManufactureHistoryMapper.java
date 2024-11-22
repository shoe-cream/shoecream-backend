package com.springboot.manufacture_history.mapper;

import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_history.dto.ManufactureHistoryResponseDto;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufactureHistoryMapper {
    default ManuFactureHistory manufactureHistoryToItemManufacture(ItemManufacture itemManufacture, Member member) {
        ManuFactureHistory manuFactureHistory = new ManuFactureHistory();
        manuFactureHistory.setMfCd(itemManufacture.getManufacture().getMfCd());
        manuFactureHistory.setMfId(itemManufacture.getManufacture().getMfId());
        manuFactureHistory.setMfItemId(itemManufacture.getMfItemId());
        manuFactureHistory.setAuthor(member.getEmployeeId());
        manuFactureHistory.setItemCd(itemManufacture.getItem().getItemCd());
        manuFactureHistory.setUnitPrice(itemManufacture.getUnitPrice());
        manuFactureHistory.setReceiveDate(itemManufacture.getCreatedAt());
        manuFactureHistory.setQty(itemManufacture.getQty());

        return manuFactureHistory;
    }


    default ManufactureHistoryResponseDto mfHistoryToMfHistoriesResponseDto(ManuFactureHistory manuFactureHistory) {
        ManufactureHistoryResponseDto.ManufactureHistoryResponseDtoBuilder response =ManufactureHistoryResponseDto.builder();
        response.mfHistoryId(manuFactureHistory.getMfHistoryId());
        response.mfId(manuFactureHistory.getMfId());
        response.mfCd(manuFactureHistory.getMfCd());
        response.qty(manuFactureHistory.getQty());
        response.createdAt(manuFactureHistory.getCreatedAt());
        response.author(manuFactureHistory.getAuthor());
        response.mfItemId(manuFactureHistory.getMfItemId());
        response.itemCd(manuFactureHistory.getItemCd());
        response.receiveDate(manuFactureHistory.getReceiveDate());
        response.unitPrice(manuFactureHistory.getUnitPrice());
        return response.build();
    }
    List<ManufactureHistoryResponseDto> mfHistoriesToMfHistoriesResponseDtos (List<ManuFactureHistory> manuFactureHistories);
}
