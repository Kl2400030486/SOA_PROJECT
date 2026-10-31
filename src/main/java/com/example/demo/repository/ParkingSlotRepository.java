package com.example.demo.repository;

import com.example.demo.entity.ParkingSlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByStatus(ParkingSlot.Status status);
}