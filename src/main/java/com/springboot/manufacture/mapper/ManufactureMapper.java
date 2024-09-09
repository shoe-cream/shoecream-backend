package com.springboot.manufacture.mapper;

import com.springboot.manufacture.dto.Dto;
import com.springboot.manufacture.entity.Manufacture;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufactureMapper {
    Manufacture postDtoToManufacture(Dto.ManufacturePostDto postDto);
    Dto.ManufactureResponseDto manufactureToResponseDto(Manufacture manufacture);
    Manufacture patchDtoToManufacture(Dto.ManufacturePatchDto patchDto);
    List<Dto.ManufactureResponseDto> manufactureToResponseDtos(List<Manufacture> manufactures);
}
