package com.springboot.manufacture.controller;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manufacturers")
@Transactional
@RequiredArgsConstructor
@Validated
public class ManufactureController {
    private final ManufactureService manufactureService;
    private final ManufactureMapper manufactureMapper;
    private final ManufactureHistoryMapper manufactureHistoryMapper;

    //제조사 등록
    @PostMapping
    public ResponseEntity createManufacture(@Valid @RequestBody List<Dto.ManufacturePostDto> postDto, Authentication authentication) {
        manufactureService.createManufacture(manufactureMapper.postDtosToManuFactrues(postDto), authentication);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 제조사 정보 조회
    @GetMapping("/{mfCd}")
    public ResponseEntity getManufacture(@PathVariable("mfCd") String mfCd, Authentication authentication) {

        Manufacture manufacture = manufactureService.findManufacture(mfCd, authentication);
        return new ResponseEntity<>(
                new SingleResponseDto<>(manufactureMapper.manufactureToResponseDto(manufacture)), HttpStatus.OK);
    }

    // 제조사 전체 조회 (pagination)
    @GetMapping
    public ResponseEntity getManufactures(@RequestParam(required = false) String sort,
                                          @RequestParam(required = false) String direction,
                                          @RequestParam(required = false) String mfCd,
                                          @RequestParam(required = false) String mfNm,
                                          @RequestParam(required = false) String email,
                                          @RequestParam(required = false) String region,
                                          @RequestParam @Positive int page,
                                          @RequestParam @Positive int size,
                                          Authentication authentication) {

        String criteria = "mfId"; //기본값
        if (sort != null) {
            //정렬 기준
            List<String> sorts = Arrays.asList("mfId", "region", "email", "mfCd", "mfNm", "createdAt", "modifiedAt", "manufactureStatus");

            if(sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }

        Page<Manufacture> manufacturePage = manufactureService.getManufactures(mfCd, mfNm, email, region, criteria, direction,page -1, size);
        List<Dto.ManufactureResponseDto> manufactureResponseDtos =
                manufactureMapper.manufactureToResponseDtos(manufacturePage.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(manufactureResponseDtos, manufacturePage), HttpStatus.OK);

    }

    // 제조사 수정
    @PatchMapping
    public ResponseEntity updateManufacture(@RequestBody List<Dto.ManufacturePatchDto> patchDtos,
                                            Authentication authentication) {

        List<Manufacture> manufactures = new ArrayList<>();

        for(Dto.ManufacturePatchDto patchDto : patchDtos) {
            Manufacture manufacture =
                    manufactureService.updateManufacture(manufactureMapper.patchDtoToManufacture(patchDto), authentication);

            manufactures.add(manufacture);
        }

        return new ResponseEntity<>(
                new SingleResponseDto<>(manufactureMapper.manufactureToResponseDtos(manufactures)), HttpStatus.OK);
    }

    // 제조사 DB에서 삭제
    @DeleteMapping("/{mfCd}")
    public ResponseEntity deleteManufacture(@PathVariable("mfCd") String mfCd, Authentication authentication) {
        manufactureService.deleteManufacture(mfCd, authentication);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    // 제조사 DB에서 삭제
    @DeleteMapping
    public ResponseEntity deleteManufactures(@RequestBody Dto.ManufactureDeleteList deleteList, Authentication authentication) {
        List<Long> deleteIds = deleteList.getMfId();

        for(Long id : deleteIds) {
            manufactureService.deleteManufactures(id, authentication);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 공급 기록 조회 ( manufacture-item 등록/수정 history ->  mfCd로 필터)
    @GetMapping("/{mfCd}/histories")
    public ResponseEntity getManufactureHistory(@PathVariable("mfCd") String mfCd,
                                                @Positive @RequestParam int page,
                                                @Positive @RequestParam int size,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(required = false) String direction,
                                                Authentication authentication) {
        String criteria = "createdAt";
        if(sort != null) {
            List<String> sorts = Arrays.asList("mfItemId", "mfCd", "createdAt", "receiveDate", "qty", "unitPrice", "author", "mfHistoryId");
            if (sorts.contains(sort)) {
                criteria = sort;
            } else {
                throw new BusinessLogicException(ExceptionCode.INVALID_SORT_FIELD);
            }
        }
        Page<ManuFactureHistory> historyPages = manufactureService.findManufactureHistories(page - 1, size, criteria, direction, mfCd, authentication);
        List<ManuFactureHistory> historyLists = historyPages.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(
                        manufactureHistoryMapper.mfHistoriesToMfHistoriesResponseDtos(historyLists),historyPages), HttpStatus.OK);
    }

    //제조사 전체 조회
    @GetMapping("/all")
    public ResponseEntity getManufactures(){
        List<Manufacture> manufactures = manufactureService.findManufactureAll();

        List<Manufacture> sortedManufactures = manufactures.stream()
                .sorted(Comparator.comparing(Manufacture::getMfNm))
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                new SingleResponseDto<>(manufactureMapper.manufactureToResponseDtos(sortedManufactures)), HttpStatus.OK);
    }
}
