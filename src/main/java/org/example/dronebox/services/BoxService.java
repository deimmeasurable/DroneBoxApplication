package org.example.dronebox.services;

import org.example.dronebox.domain.Box;
import org.example.dronebox.domain.Item;
import org.example.dronebox.dto.BoxDto;
import org.example.dronebox.dto.BoxRequest;
import org.example.dronebox.dto.BoxResponse;
import org.example.dronebox.dto.ItemRequest;

import java.util.List;

public interface BoxService {
    BoxResponse createBox(BoxRequest boxRequest);
    int getBattery(String txref);
    List<Item> loadItems(String txref, List<ItemRequest> itemsDto);
    List<Item> getLoadedItems(String txref);
    List<BoxDto> getAvailableForLoading();
    Box getBox(String txref);
}
