package com.springboot.item.service;

import com.springboot.item.entity.Item;
import com.springboot.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void createItem(Item item) {
        itemRepository.save(item);
    }
}
