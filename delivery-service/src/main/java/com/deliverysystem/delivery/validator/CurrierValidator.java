package com.deliverysystem.delivery.validator;

import com.deliverysystem.delivery.advice.exceptions.CurrierFoundException;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrierValidator {

    private final CurrierRepository currierRepository;

    public void checkCourierEmailNotExists(String email) {
        currierRepository.findByEmail(email).ifPresent(c -> {
            throw new CurrierFoundException("Courier with email " + email + " already exists.");
        });
    }
}
