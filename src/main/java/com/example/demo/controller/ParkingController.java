package com.example.demo.controller;

import com.example.demo.entity.ParkingSlot;
import com.example.demo.entity.ParkingZone;
import com.example.demo.service.ParkingService;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/zones")
    public ParkingZone createZone(@Valid @RequestBody ParkingZone zone) {
        return parkingService.createZone(zone);
    }

    @GetMapping("/zones")
    public List<ParkingZone> getZones() {
        return parkingService.getZones();
    }

    @GetMapping("/zones/{id}")
    public ParkingZone getZone(@PathVariable Long id) {
        return parkingService.getZone(id);
    }

    @PutMapping("/zones/{id}/capacity")
    public ParkingZone updateZoneCapacity(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        return parkingService.updateZoneCapacity(id, request.get("capacity"));
    }

    @PostMapping("/slots")
    public ParkingSlot createSlot(@Valid @RequestBody ParkingSlot slot) {
        return parkingService.createSlot(slot);
    }

    @DeleteMapping("/slots/{id}")
    public void deleteSlot(@PathVariable Long id) {
        parkingService.deleteSlot(id);
    }

    @GetMapping("/slots")
    public List<ParkingSlot> getSlots() {
        return parkingService.getSlots();
    }

    @GetMapping("/slots/available")
    public List<ParkingSlot> getAvailableSlots() {
        return parkingService.getAvailableSlots();
    }

    @GetMapping("/occupancy")
    public Map<String, Long> getOccupancy() {
        return parkingService.getOccupancy();
    }

    @PutMapping("/slots/{id}/status")
    public ParkingSlot updateSlotStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        ParkingSlot.Status status;
        try {
            status = ParkingSlot.Status.valueOf(request.get("status").toUpperCase());
        } catch (RuntimeException exception) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Status must be AVAILABLE, RESERVED, or OCCUPIED");
        }
        return parkingService.updateSlotStatus(id, status);
    }
}