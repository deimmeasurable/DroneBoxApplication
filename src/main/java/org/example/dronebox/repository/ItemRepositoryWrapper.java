package org.example.dronebox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dronebox.domain.Item;
import org.example.dronebox.exception.BoxException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryWrapper {
    private final ItemRepository itemRepository;

    public List<Item> saveAll(final List<Item> items) {
      return   this.itemRepository.saveAll(items);
    }
    @Transactional(readOnly = true)
    public Item findOneWithNotFoundDetection(final Long id) {
        return this.itemRepository.findById(id).orElseThrow(() -> new BoxException("Item Information not found: "+id));
    }
}
