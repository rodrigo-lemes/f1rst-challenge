package com.f1rst.challenge.address_finder.service;

import com.f1rst.challenge.address_finder.api.model.AddressZipRequest;
import com.f1rst.challenge.address_finder.client.AddressFeignClient;
import com.f1rst.challenge.address_finder.client.response.AddressLogResponse;
import com.f1rst.challenge.address_finder.repository.AddressRepository;
import com.f1rst.challenge.address_finder.repository.entity.AddressQueryLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final AddressFeignClient addressFeignClient;
    private final ObjectMapper objectMapper;

    public AddressService(AddressRepository addressRepository, AddressFeignClient addressFeignClient, ObjectMapper objectMapper) {
        this.addressRepository = addressRepository;
        this.addressFeignClient = addressFeignClient;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void genAddressData(AddressZipRequest addressZipRequest) {

        log.info("Searching for zipCode={}", addressZipRequest.getZipCode());

        AddressLogResponse addressData = addressFeignClient.getAddressData(addressZipRequest.getZipCode());

        if (addressData == null || "true".equalsIgnoreCase(addressData.getErro())) {
            log.info("Address not found for zipCode={}", addressZipRequest.getZipCode());
            throw new IllegalArgumentException("Address not found for zipCode: " + addressZipRequest.getZipCode());
        }

        log.info("Found address for zipCode={}", addressZipRequest.getZipCode());

        addressData.setSearchedAt(LocalDateTime.now());

        addressRepository.save(objectMapper.convertValue(addressData, AddressQueryLogEntity.class));

        log.info("Address saved successfully for zipCode={}", addressZipRequest.getZipCode());
    }
}