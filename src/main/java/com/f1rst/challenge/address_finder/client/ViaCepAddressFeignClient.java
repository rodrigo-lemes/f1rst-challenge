package com.f1rst.challenge.address_finder.client;

import com.f1rst.challenge.address_finder.client.response.AddressLogClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "addressFeignClient",
        url = "${client.viacep.url}"
)
public interface ViaCepAddressFeignClient {

    @GetMapping("/ws/{zipCode}/json/")
    public AddressLogClientResponse getAddressData(@PathVariable("zipCode") String zipCode);
}