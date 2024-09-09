package com.springboot.item_manufacture.service;

import com.springboot.item.repository.ItemRepository;
import com.springboot.item_manufacture.mapper.ItemMfMapper;
import com.springboot.item_manufacture.repository.ItemMfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemManufacture {
    private final ItemMfRepository itemMfRepository;
    private final ItemMfMapper itemMfMapper;



}
