package com.f1rst.challenge.address_finder.controller;

import com.f1rst.challenge.address_finder.api.AddressApi;
import com.f1rst.challenge.address_finder.api.model.AddressResponse;
import com.f1rst.challenge.address_finder.service.AddressService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping
@Slf4j
public class AddressController implements AddressApi {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Override
    public ResponseEntity<AddressResponse> getAddressByZipCode(String zipCode) {
        log.info("Starting address search for zipCode={}", zipCode);

        try {
            var result = addressService.getAddressByZipCode(zipCode);

            log.info("Address located successfully for zipCode={}", zipCode);

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException exception) {
            log.info("Address not found for zipCode={}, message={}", zipCode, exception.getMessage());
            return ResponseEntity.notFound().build();
        } catch (FeignException.BadRequest exception) {
            log.error("Bad request when calling external address API for zipCode={}, message={}",
                    zipCode,
                    exception.getMessage());

            return ResponseEntity.badRequest().build();
        } catch (FeignException exception) {
            log.error("External address API error for zipCode={}, message={}",
                    zipCode,
                    exception.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}