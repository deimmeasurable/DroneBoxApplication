package org.example.dronebox.repository;

import org.example.dronebox.domain.Box;
import org.example.dronebox.domain.BoxState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {
    Optional<Box> findById(Long id);
    Optional<Box> findByTxref(String txref);
    Boolean existsByTxref(String txref);

    List<Box> findByStateAndBatteryGreaterThanEqual(BoxState state, int battery);
}
