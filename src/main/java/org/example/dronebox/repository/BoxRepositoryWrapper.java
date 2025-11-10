package org.example.dronebox.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dronebox.domain.Box;
import org.example.dronebox.domain.BoxState;
import org.example.dronebox.exception.BoxException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoxRepositoryWrapper {
private final BoxRepository boxRepository;

    @Transactional(readOnly = true)
    public Box findOneWithNotFoundDetection(final Long id) {
        return this.boxRepository.findById(id).orElseThrow(() -> new BoxException("Box Information not found: "+id));
    }

    public Box getBox(String txref) {
        return boxRepository.findByTxref(txref).orElseThrow(() -> new BoxException("Box not found"));
    }
    public Boolean existsByTxref(String txref) {
        return boxRepository.existsByTxref(txref);
    }
    public List<Box> getAvailableForLoading() {
// available meaning state IDLE and battery >= 25
        int batteryMinLevel= 25;
        return boxRepository.findAll().stream()
                .filter(b -> b.getState() == BoxState.IDLE && b.getBattery() >= batteryMinLevel)
                .collect(Collectors.toList());
    }
    public Box save(Box box) {
        boxRepository.save(box);
        return box;
    }

    public void saveAll(final List<Box> cards) {
        this.boxRepository.saveAll(cards);
    }

    public void delete(final Box box) {
        this.boxRepository.delete(box);

    }

    @Transactional(readOnly = true)
    public Page<Box> findAll(Pageable pageable) {
        return this.boxRepository.findAll(pageable);
    }
}

