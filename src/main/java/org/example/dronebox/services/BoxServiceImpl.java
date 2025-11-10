package org.example.dronebox.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dronebox.domain.Box;
import org.example.dronebox.domain.BoxState;
import org.example.dronebox.domain.Item;
import org.example.dronebox.dto.BoxDto;
import org.example.dronebox.dto.BoxRequest;
import org.example.dronebox.dto.BoxResponse;
import org.example.dronebox.dto.ItemRequest;
import org.example.dronebox.exception.BoxException;
import org.example.dronebox.repository.BoxRepository;
import org.example.dronebox.repository.BoxRepositoryWrapper;
import org.example.dronebox.repository.ItemRepositoryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoxServiceImpl implements BoxService {

    private final BoxRepository boxRepository;
    private final BoxRepositoryWrapper boxRepositoryWrapper;
    private final ItemRepositoryWrapper itemRepositoryWrapper;

    public BoxResponse createBox(BoxRequest boxRequest) {
        if (boxRepositoryWrapper.existsByTxref(boxRequest.getTxref())) {
            throw new IllegalArgumentException("Box with txref already exists");
        }
        Box box = new Box();
        box.setTxref(boxRequest.getTxref());
        box.setWeightLimit(boxRequest.getWeightLimit());
        box.setBattery(boxRequest.getBattery());
        box.setState(BoxState.IDLE);

        BoxResponse boxResponse = BoxResponse.builder()
                .message("Box created successfully")
                .txref(box.getTxref())
                .build();

        boxRepository.save(box);

        return boxResponse;
    }


    public Box getBox(String txref) {
        return boxRepository.findByTxref(txref)
                .orElseThrow(() -> new IllegalArgumentException("Box not found: " + txref));
    }


    public List<BoxDto> getAvailableForLoading() {
        return boxRepository.findAll().stream()
                .filter(b -> b.getState() == BoxState.IDLE && b.getBattery() >= 25)
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    public int getBattery(String txref) {
        Box box = getBox(txref);
        return box.getBattery();
    }


    @Transactional
    public List<Item> loadItems(String txref, List<ItemRequest> itemRequests) {

        Box box = getBox(txref);

        if (box.getBattery() < 25) {
            throw new IllegalStateException("Battery too low to start loading");
        }
        if (!(box.getState() == BoxState.IDLE || box.getState() == BoxState.LOADING)) {
            throw new IllegalStateException("Box not in a loadable state");
        }

        for (ItemRequest req : itemRequests) {
            validateItemFields(req.getName(), req.getCode());
        }

        int currentWeight = box.getItems().stream()
                .mapToInt(Item::getWeight)
                .sum();

        int incomingWeight = itemRequests.stream()
                .mapToInt(ItemRequest::getWeight)
                .sum();

        if (currentWeight + incomingWeight > box.getWeightLimit()) {
            throw new IllegalStateException("Exceeds weight limit");
        }


        box.setState(BoxState.LOADING);
        boxRepository.save(box);

        List<Item> created = itemRequests.stream().map(req -> {
            Item it = new Item();
            it.setName(req.getName());
            it.setWeight(req.getWeight());
            it.setCode(req.getCode());
            it.setBox(box);
            return it;
        }).collect(Collectors.toList());

        List<Item> savedItems = itemRepositoryWrapper.saveAll(created);

        box.getItems().addAll(savedItems);


        box.setState(BoxState.LOADED);
        boxRepository.save(box);

        return savedItems;
    }

    public List<Item> getLoadedItems(String txref) {
        Box box = getBox(txref);
        return box.getItems();
    }

    private void validateItemFields(String name, String code) {
        if (name == null || !name.matches("[A-Za-z0-9_-]+")) {
            throw new BoxException(
                    "Invalid item name: " + name +
                            ". Allowed only letters, numbers, hyphen '-' and underscore '_'."
            );
        }

        if (code == null || !code.matches("[A-Z0-9_]+")) {
            throw new BoxException(
                    "Invalid item code: " + code +
                            ". Allowed only UPPERCASE letters, numbers and underscore '_'."
            );
        }
    }

    private BoxDto toDto(Box box) {
        BoxDto dto = new BoxDto();
        dto.setTxref(box.getTxref());
        dto.setWeightLimit(box.getWeightLimit());
        dto.setBattery(box.getBattery());
        dto.setState(box.getState());
        return dto;

    }
}
