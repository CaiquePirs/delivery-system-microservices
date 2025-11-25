package com.systemdelivery.authentication.validator;

import com.systemdelivery.authentication.controller.advice.exceptions.ErrorLoginException;
import com.systemdelivery.authentication.controller.advice.exceptions.ErrorRegisterException;
import com.systemdelivery.authentication.controller.dto.InternalLoginDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationValidator {

    public void validateInternalServiceLogin(InternalLoginDTO internalLoginDTO) {
        if (internalLoginDTO.clientId() == null || internalLoginDTO.clientId().isBlank()) {
            throw new ErrorLoginException("Client Id cannot be empty");
        }

        if (internalLoginDTO.clientSecret() == null || internalLoginDTO.clientSecret().isBlank()) {
            throw new ErrorLoginException("Client Secret cannot be empty");
        }
    }

    public void validateIfUserIsNull(Object object) {
        if (object == null) {
            throw new ErrorRegisterException("Error creating user");
        }
    }
}
