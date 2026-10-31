package com.example.demo.service;

import com.example.demo.entity.ParkingSlot;
import com.example.demo.entity.ParkingZone;
import com.example.demo.repository.ParkingSlotRepository;
import com.example.demo.repository.ParkingZoneRepository;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ParkingService {

    private final ParkingZoneRepository zoneRepository;
    private final ParkingSlotRepository slotRepository;

    public ParkingService(
            ParkingZoneRepository zoneRepository,
            ParkingSlotRepository slotRepository) {
        this.zoneRepository = zoneRepository;
        this.slotRepository = slotRepository;
    }

    public ParkingZone createZone(ParkingZone zone) {
        return zoneRepository.save(zone);
    }

    public List<ParkingZone> getZones() {
        return zoneRepository.findAll();
    }

    public ParkingZone getZone(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zone not found"));
    }

    public ParkingZone updateZoneCapacity(Long id, int capacity) {
        if (capacity < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Capacity cannot be negative");
        }

        ParkingZone zone = getZone(id);
        zone.setCapacity(capacity);
        return zoneRepository.save(zone);
    }

    public ParkingSlot createSlot(ParkingSlot slot) {
        if (slot.getStatus() == null) {
            slot.setStatus(ParkingSlot.Status.AVAILABLE);
        }
        return slotRepository.save(slot);
    }

    public void deleteSlot(Long id) {
        if (!slotRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found");
        }
        slotRepository.deleteById(id);
    }

    public List<ParkingSlot> getSlots() {
        return slotRepository.findAll();
    }

    public List<ParkingSlot> getAvailableSlots() {
        return slotRepository.findByStatus(ParkingSlot.Status.AVAILABLE);
    }

    public ParkingSlot updateSlotStatus(Long id, ParkingSlot.Status status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        ParkingSlot slot = slotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
        slot.setStatus(status);
        return slotRepository.save(slot);
    }

    public Map<String, Long> getOccupancy() {
        List<ParkingSlot> slots = slotRepository.findAll();
        return Map.of(
                "totalSlots", (long) slots.size(),
                "availableSlots", countByStatus(slots, ParkingSlot.Status.AVAILABLE),
                "reservedSlots", countByStatus(slots, ParkingSlot.Status.RESERVED),
                "occupiedSlots", countByStatus(slots, ParkingSlot.Status.OCCUPIED));
    }

    private long countByStatus(List<ParkingSlot> slots, ParkingSlot.Status status) {
        return slots.stream()
                .filter(slot -> slot.getStatus() == status)
                .count();
    }
}