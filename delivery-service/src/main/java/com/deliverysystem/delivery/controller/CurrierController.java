package com.deliverysystem.delivery.controller;

import com.deliverysystem.delivery.controller.dtos.CurrierRequestDTO;
import com.deliverysystem.delivery.controller.dtos.CurrierResponseDTO;
import com.deliverysystem.delivery.mapper.CurrierMapper;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.service.CurrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/curriers")
@RequiredArgsConstructor
public class CurrierController {

    private final CurrierService service;
    private final CurrierMapper mapper;

    @PostMapping
    public ResponseEntity<CurrierResponseDTO> currier(@RequestBody CurrierRequestDTO dto){
        Currier currier = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToResponse(currier));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrierResponseDTO> findCurrierById(@PathVariable(name = "id") UUID currierId){
        Currier currier = service.findById(currierId);
        return ResponseEntity.ok(mapper.mapToResponse(currier));
    }
}
