package com.springboot.manufacture.mapper;

import com.springboot.manufacture.dto.Dto;
import com.springboot.manufacture.entity.Manufacture;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManufactureMapper {
    Manufacture postDtoToManufacture(Dto.ManufacturePostDto postDto);
}
