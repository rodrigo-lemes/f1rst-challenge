package com.f1rst.challenge.address_finder.controller;

import com.f1rst.challenge.address_finder.api.AddressApi;
import com.f1rst.challenge.address_finder.api.model.AddressZipRequest;
import com.f1rst.challenge.address_finder.service.AddressService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/address")
@Slf4j
public class AddressController implements AddressApi {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/zip")
    @Override
    public ResponseEntity<Void> genAddressData(AddressZipRequest addressZipRequest) {
        log.info("Starting address search for zipCode={}", addressZipRequest.getZipCode());

        try {
            addressService.genAddressData(addressZipRequest);

            log.info("Address logging process finished successfully for zipCode={}", addressZipRequest.getZipCode());

            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            log.info("Address not found for zipCode={}", addressZipRequest.getZipCode());
            return ResponseEntity.notFound().build();
        } catch (FeignException.BadRequest exception) {
            log.error("Bad request when calling external address API for zipCode={}, message={}", addressZipRequest.getZipCode(), exception.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (FeignException exception) {
            log.error("External address API error for zipCode={}, message={}", addressZipRequest.getZipCode(), exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (DataAccessException exception) {
            log.error("Database error while saving address log for zipCode={}, message={}", addressZipRequest.getZipCode(), exception.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}