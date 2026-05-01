package com.f1rst.challenge.address_finder.service;

import com.f1rst.challenge.address_finder.api.model.AddressResponse;
import com.f1rst.challenge.address_finder.api.model.AddressSource;
import com.f1rst.challenge.address_finder.client.ViaCepAddressFeignClient;
import com.f1rst.challenge.address_finder.client.response.AddressLogClientResponse;
import com.f1rst.challenge.address_finder.repository.AddressRepository;
import com.f1rst.challenge.address_finder.repository.entity.AddressQueryLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.f1rst.challenge.address_finder.mapper.AddressMapper.toAddressResponse;

@Service
@Slf4j
public class AddressService {

    private final List<String> allowedStates;
    private final AddressRepository addressRepository;
    private final ViaCepAddressFeignClient addressFeignClient;
    private final ObjectMapper objectMapper;

    public AddressService(
            @Value("#{'${coverage.allowed-states}'.split(',')}") List<String> allowedStates,
            AddressRepository addressRepository,
            ViaCepAddressFeignClient addressFeignClient,
            ObjectMapper objectMapper
    ) {
        this.allowedStates = allowedStates;
        this.addressRepository = addressRepository;
        this.addressFeignClient = addressFeignClient;
        this.objectMapper = objectMapper;
    }

    public AddressResponse getAddressByZipCode(String zipCode) {
        zipCode = normalizeZipCode(zipCode);
        log.info("Searching for zipCode={}", zipCode);

        Optional<AddressQueryLogEntity> dataBaseAddressData = addressRepository.findByCep(zipCode);

        if (dataBaseAddressData.isPresent()) {
            log.info("Address found in database for zipCode={}", zipCode);

            return toAddressResponse(
                    dataBaseAddressData.get(),
                    AddressSource.DATABASE
            );
        }

        AddressLogClientResponse onlineData =
                addressFeignClient.getAddressData(zipCode);

        if (onlineData == null || "true".equalsIgnoreCase(onlineData.getErro())) {
            log.info("Address not found for zipCode={}", zipCode);
            throw new IllegalArgumentException("Address not found for zipCode: " + zipCode);
        }

        log.info("Address found in external API for zipCode={}", zipCode);

        onlineData.setSearchedAt(LocalDateTime.now());
        onlineData.setCovered(allowedStates.contains(onlineData.getUf()));

        AddressQueryLogEntity entity = objectMapper.convertValue(
                onlineData,
                AddressQueryLogEntity.class
        );

        entity.setCep(normalizeZipCode(entity.getCep()));

        entity = addressRepository.save(entity);

        log.info("Address saved successfully for zipCode={}", onlineData.getCep());

        return toAddressResponse(
                entity,
                AddressSource.EXTERNAL_API
        );
    }

    private String normalizeZipCode(String zipCode) {
        return zipCode.replace("-", "");
    }
}