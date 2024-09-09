package com.springboot.item.controller;


import com.springboot.item.dto.Dto;
import com.springboot.item.mapper.ItemMapper;
import com.springboot.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/item")
@Validated
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity createItem(@Valid @RequestBody Dto.ItemPostDto postDto) {
        itemMapper.itemPostDtoToItem(postDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
