package org.example.dronebox.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dronebox.domain.Item;
import org.example.dronebox.dto.BoxDto;
import org.example.dronebox.dto.BoxRequest;
import org.example.dronebox.dto.BoxResponse;
import org.example.dronebox.dto.ItemRequest;
import org.example.dronebox.services.BoxService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value ="/api/box")
public class BoxDroneController {
    private final BoxService boxService;

    @PostMapping
    public ResponseEntity<BoxResponse> createBox(@Valid @RequestBody BoxRequest dto) {
        BoxResponse created = boxService.createBox(dto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{txref}/load")
    public ResponseEntity<List<Item>> loadBox(
            @PathVariable String txref,
            @Valid @RequestBody List<@Valid ItemRequest> items
    ) {
        List<Item> loaded = boxService.loadItems(txref, items);
        return ResponseEntity.ok(loaded);
    }

    @GetMapping("/{txref}/items")
    public ResponseEntity<List<Item>> getItems(@PathVariable String txref) {
        List<Item> items = boxService.getLoadedItems(txref);
        return ResponseEntity.ok(items);
    }


    @GetMapping("/available")
    public ResponseEntity<List<BoxDto>> getAvailable() {
        List<BoxDto> boxes = boxService.getAvailableForLoading();
        return ResponseEntity.ok(boxes);
    }

    @GetMapping("/{txref}/battery")
    public ResponseEntity<Integer> getBattery(@PathVariable String txref) {
        int battery = boxService.getBattery(txref);
        return ResponseEntity.ok(battery);
    }
}
