package org.example.dronebox.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dronebox.domain.Box;
import org.example.dronebox.domain.BoxState;
import org.example.dronebox.domain.Item;
import org.example.dronebox.dto.BoxDto;
import org.example.dronebox.dto.BoxRequest;
import org.example.dronebox.dto.BoxResponse;
import org.example.dronebox.dto.ItemRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class BoxServiceImplTest {
    @Autowired
private BoxService boxService;

    @Test
    void testThatABoxCanBeCreated() {
        BoxRequest request = new BoxRequest();
        request.setTxref("BOX12345");
        request.setBattery(90);
        request.setWeightLimit(500);
        request.setBoxState(BoxState.IDLE.name());

        BoxResponse created = boxService.createBox(request);

        Assertions.assertNotNull(created);
        Assertions.assertEquals("BOX12345", created.getTxref());
        Assertions.assertEquals("Box created successfully", created.getMessage());

    }


    @Test
    void testLoadItemsSuccessfully() {

        BoxRequest box = new BoxRequest();
        box.setTxref("LOAD123");
        box.setWeightLimit(500);
        box.setBattery(90);
        box.setBoxState(BoxState.IDLE.name());
        boxService.createBox(box);

        ItemRequest item1 = new ItemRequest();
        item1.setName("item-one");
        item1.setWeight(100);
        item1.setCode("ITEM_1");

        ItemRequest item2 = new ItemRequest();
        item2.setName("toy_2");
        item2.setWeight(150);
        item2.setCode("TOY_2");

        List<ItemRequest> items = List.of(item1, item2);

        var loaded = boxService.loadItems("LOAD123", items);

        Assertions.assertEquals(2, loaded.size());
        Assertions.assertEquals("item-one", loaded.get(0).getName());
        Assertions.assertEquals("ITEM_1", loaded.get(0).getCode());
        Assertions.assertEquals(BoxState.LOADED,
                boxService.getBox("LOAD123").getState());
    }


    @Test
    void testGetLoadedItems() {
        BoxRequest box = new BoxRequest();
        box.setTxref("ITEMCHECK");
        box.setBattery(80);
        box.setWeightLimit(500);
        box.setBoxState(BoxState.IDLE.name());
        boxService.createBox(box);

        ItemRequest req = new ItemRequest();
        req.setName("aaa");
        req.setCode("AA1");
        req.setWeight(100);

        boxService.loadItems("ITEMCHECK", List.of(req));

        List<Item> loaded = boxService.getLoadedItems("ITEMCHECK");

        Assertions.assertEquals(1, loaded.size());
        Assertions.assertEquals("aaa", loaded.get(0).getName());
    }


    @Test
    void testGetAvailableForLoading() {

        BoxRequest b1 = new BoxRequest();
        b1.setTxref("FREE1");
        b1.setBattery(80);
        b1.setWeightLimit(500);
        b1.setBoxState(BoxState.IDLE.name());
        boxService.createBox(b1);

        BoxRequest b2 = new BoxRequest();
        b2.setTxref("FREE2");
        b2.setBattery(10);
        b2.setWeightLimit(500);
        b2.setBoxState(BoxState.IDLE.name());
        boxService.createBox(b2);

        List<BoxDto> available = boxService.getAvailableForLoading();

        Assertions.assertEquals(2, available.size());
        Assertions.assertEquals("FREE1", available.get(0).getTxref());
    }


    @Test
    void testGetBatteryLevel() {
        BoxRequest box = new BoxRequest();
        box.setTxref("BATT1");
        box.setBattery(55);
        box.setWeightLimit(500);
        box.setBoxState(BoxState.IDLE.name());

        boxService.createBox(box);

        int level = boxService.getBattery("BATT1");

        Assertions.assertEquals(55, level);
    }


    @Test
    void testWeightLimitPreventsLoading() {
        BoxRequest box = new BoxRequest();
        box.setTxref("HEAVY1");
        box.setBattery(80);
        box.setWeightLimit(200);
        box.setBoxState(BoxState.IDLE.name());
        boxService.createBox(box);

        ItemRequest req = new ItemRequest();
        req.setName("big");
        req.setCode("BIG01");
        req.setWeight(300);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> boxService.loadItems("HEAVY1", List.of(req))
        );
    }

    @Test
    void testLowBatteryPreventsLoading() {
        BoxRequest box = new BoxRequest();
        box.setTxref("LOWBAT");
        box.setBattery(10);
        box.setWeightLimit(500);
        box.setBoxState(BoxState.IDLE.name());
        boxService.createBox(box);

        ItemRequest req = new ItemRequest();
        req.setName("toy");
        req.setCode("TOY01");
        req.setWeight(50);

        Assertions.assertThrows(
                IllegalStateException.class,
                () -> boxService.loadItems("LOWBAT", List.of(req))
        );
    }

}