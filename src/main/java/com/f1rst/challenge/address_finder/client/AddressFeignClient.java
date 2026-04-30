package com.f1rst.challenge.address_finder.client;

import com.f1rst.challenge.address_finder.client.response.AddressLogResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "addressFeignClient",
        url = "${client.viacep.url}"
)
public interface AddressFeignClient {

    @GetMapping("/ws/{zipCode}/json/")
    AddressLogResponse getAddressData(@PathVariable("zipCode") String zipCode);
}