package com.springboot.manufacture.controller;

import com.springboot.manufacture.dto.Dto;
import com.springboot.manufacture.entity.Manufacture;
import com.springboot.manufacture.mapper.ManufactureMapper;
import com.springboot.manufacture.service.ManufactureService;
import com.springboot.manufacture_history.entity.ManuFactureHistory;
import com.springboot.manufacture_history.mapper.ManufactureHistoryMapper;
import com.springboot.response.MultiResponseDto;
import com.springboot.response.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/manufacturers")
@Transactional
@RequiredArgsConstructor
@Validated
public class ManufactureController {
    private final ManufactureService manufactureService;
    private final ManufactureMapper manufactureMapper;
    private final ManufactureHistoryMapper manufactureHistoryMapper;

    @PostMapping
    public ResponseEntity createManufacture(@Valid @RequestBody List<Dto.ManufacturePostDto> postDto, Authentication authentication) {
        manufactureService.createManufacture(manufactureMapper.postDtosToManuFactrues(postDto), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 제조사 정보 조회
    @GetMapping("/{mfId}")
    public ResponseEntity getManufacture(@PathVariable @Positive long mfId, Authentication authentication) {
        Manufacture manufacture = manufactureService.getManufacture(mfId, authentication);
        return new ResponseEntity<>(
                new SingleResponseDto<>(manufactureMapper.manufactureToResponseDto(manufacture)), HttpStatus.OK);
    }

    // 제조사 전체 조회 최신순(선택사항)
    @GetMapping
    public ResponseEntity getManufactures(@RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam @Positive int page,
                                          @RequestParam @Positive int size,
                                          Authentication authentication) {
        Page<Manufacture> manufacturePage = manufactureService.getManufactures(sortBy, page -1, size, authentication);
        List<Dto.ManufactureResponseDto> manufactureResponseDtos =
                manufactureMapper.manufactureToResponseDtos(manufacturePage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(manufactureResponseDtos, manufacturePage), HttpStatus.OK);

    }

    // 제조사 수정
    @PatchMapping("/{mfId}")
    public ResponseEntity updateManufacture(@PathVariable @Positive long mfId,
                                            @RequestBody Dto.ManufacturePatchDto patchDto,
                                            Authentication authentication) {
        patchDto.setMfId(mfId);
        Manufacture manufacture =
                manufactureService.updateManufacture(manufactureMapper.patchDtoToManufacture(patchDto), authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(manufactureMapper.manufactureToResponseDto(manufacture)), HttpStatus.OK);
    }

    // 제조사 삭제
    @DeleteMapping("/{mfId}")
    public ResponseEntity deleteManufacture(@PathVariable @Positive long mfId, Authentication authentication) {
        manufactureService.deleteManufacture(mfId, authentication);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    // 공급 기록 조회
    @GetMapping("/{mfId}/histories")
    public ResponseEntity getManufactureHistory(@Positive @PathVariable("mfId") Long mfId,
                                                @Positive @RequestParam int page,
                                                @Positive @RequestParam int size,
                                                Authentication authentication) {
        Page<ManuFactureHistory> historyPages = manufactureService.findManufactureHistories(page - 1, size, mfId, authentication);
        List<ManuFactureHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        manufactureHistoryMapper.mfHistoriesToMfHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }
}
