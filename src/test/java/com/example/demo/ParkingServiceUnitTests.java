package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.demo.entity.ParkingSlot;
import com.example.demo.repository.ParkingSlotRepository;
import com.example.demo.repository.ParkingZoneRepository;
import com.example.demo.service.ParkingService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkingServiceUnitTests {

    @Mock
    private ParkingZoneRepository zoneRepository;

    @Mock
    private ParkingSlotRepository slotRepository;

    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
        parkingService = new ParkingService(zoneRepository, slotRepository);
    }

    @Test
    void findsAvailableSlots() {
        ParkingSlot availableSlot = new ParkingSlot();
        availableSlot.setSlotNumber("A-01");
        availableSlot.setStatus(ParkingSlot.Status.AVAILABLE);
        when(slotRepository.findByStatus(ParkingSlot.Status.AVAILABLE))
                .thenReturn(List.of(availableSlot));

        List<ParkingSlot> result = parkingService.getAvailableSlots();

        assertEquals(1, result.size());
        assertEquals("A-01", result.get(0).getSlotNumber());
    }

    @Test
    void calculatesOccupancyCounts() {
        ParkingSlot available = new ParkingSlot();
        available.setStatus(ParkingSlot.Status.AVAILABLE);
        ParkingSlot reserved = new ParkingSlot();
        reserved.setStatus(ParkingSlot.Status.RESERVED);
        ParkingSlot occupied = new ParkingSlot();
        occupied.setStatus(ParkingSlot.Status.OCCUPIED);
        when(slotRepository.findAll()).thenReturn(List.of(available, reserved, occupied));

        Map<String, Long> result = parkingService.getOccupancy();

        assertEquals(3L, result.get("totalSlots"));
        assertEquals(1L, result.get("availableSlots"));
        assertEquals(1L, result.get("reservedSlots"));
        assertEquals(1L, result.get("occupiedSlots"));
    }
}
