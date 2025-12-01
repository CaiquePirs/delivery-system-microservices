package com.deliverysystem.delivery.validator;

import com.deliverysystem.delivery.controller.advice.exceptions.CurrierFoundException;
import com.deliverysystem.delivery.model.Currier;
import com.deliverysystem.delivery.repositories.CurrierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CurrierValidatorTest {

    @Mock private CurrierRepository currierRepository;
    @InjectMocks private CurrierValidator currierValidator;

    @Test
    void shouldPassInWhenCheckCourierEmailNotExists(){
        String email = "test@gmail.com";

        when(currierRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> currierValidator.checkCourierEmailNotExists(email));

        verify(currierRepository, timeout(1)).findByEmail(email);
    }

    @Test
    void shouldTrowExceptionWhenCurrierEmailExist(){
        String email = "test@gmail.com";

        Currier currier = new Currier();
        currier.setEmail(email);

        when(currierRepository.findByEmail(email)).thenReturn(Optional.of(currier));

        CurrierFoundException ex = assertThrows(
                CurrierFoundException.class,
                () -> currierValidator.checkCourierEmailNotExists(email)
        );

        assertEquals("Courier with email "  + email + " already exists.", ex.getMessage());

        verify(currierRepository, times(1)).findByEmail(email);
    }


}
