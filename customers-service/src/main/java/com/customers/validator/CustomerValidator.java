package com.customers.validator;

import com.customers.controller.advice.exceptions.CustomerFoundException;
import com.customers.controller.advice.exceptions.CustomerNotAuthorizedException;
import com.customers.controller.advice.exceptions.NotFoundException;
import com.customers.model.Customer;
import com.customers.model.enums.AuditStatus;
import com.customers.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerValidator {

    private final CustomerRepository customerRepository;

    public void checkIfExistCustomerWithSameEmail(String email) {
        customerRepository.findByEmail(email).ifPresent(customer -> {
            throw new CustomerFoundException("This email already exist");
        });
    }

    public void validateAuthenticatedCustomerOwnership(Jwt auth, UUID addressId){
        Customer customer = resolverAndFindCustomerLogged(auth);

        if(!isCustomerOwner(addressId, customer) && !isInternalService()){
            throw new CustomerNotAuthorizedException("Customer not authorized to perform this request");
        }
    }

    public Customer resolverAndFindCustomerLogged(Jwt auth) {
        UUID customerIdLogged = getCustomerIdLogged(auth);
        if(customerIdLogged == null) {
            throw new CustomerNotAuthorizedException("Customer not authorized to perform this request ");
        }
        return findCustomerById(customerIdLogged);
    }

    public Customer findCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .filter(c -> !c.getStatus().equals(AuditStatus.DELETED))
                .orElseThrow(() -> new NotFoundException("Customer ID not found"));
    }

    private UUID getCustomerIdLogged(Jwt authJwt) {
        String customerId = authJwt.getClaimAsString("customer_id");
        return customerId != null ? UUID.fromString(customerId) : null;
    }

    private boolean isCustomerOwner(UUID addressId, Customer customer) {
        return customer.getAddresses().stream()
                .anyMatch(address ->  address.getId().equals(addressId));
    }

    private boolean isInternalService() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt token = jwtAuth.getToken();
            String scope = token.getClaimAsString("scope");

            return scope != null && scope.contains("internal-service");
        }
        return false;
    }
}
