package com.springboot.buyer.mapper;

import com.springboot.buyer.dto.Dto;
import com.springboot.buyer.entity.Buyer;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BuyerMapper {

    Buyer buyerPostDtoToBuyer(Dto.BuyerPostDto postDto);

    List<Buyer> postDtosToBuyerItems(List<Dto.BuyerPostDto> postDtoList);

    Buyer buyerPatchDtoToBuyer(Dto.BuyerPatchDto patchDto);

    Dto.BuyerResponse buyerToBuyerResponseDto(Buyer buyer);

    List<Dto.BuyerResponse> buyerToBuyerResponseDtos(List<Buyer> buyers);
}
