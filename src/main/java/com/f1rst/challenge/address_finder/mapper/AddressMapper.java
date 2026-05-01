package com.f1rst.challenge.address_finder.mapper;

import com.f1rst.challenge.address_finder.api.model.AddressResponse;
import com.f1rst.challenge.address_finder.api.model.AddressSource;
import com.f1rst.challenge.address_finder.repository.entity.AddressQueryLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public static AddressResponse toAddressResponse(
            AddressQueryLogEntity entity,
            AddressSource source
    ) {
        AddressResponse response = new AddressResponse();

        response.setZipCode(entity.getCep());
        response.setStreet(entity.getLogradouro());
        response.setComplement(entity.getComplemento());
        response.setNeighborhood(entity.getBairro());
        response.setCity(entity.getLocalidade());
        response.setState(entity.getUf());
        response.setCovered(entity.getCovered());
        response.setSource(source);

        return response;
    }
}