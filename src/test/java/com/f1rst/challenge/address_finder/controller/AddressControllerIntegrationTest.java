package com.f1rst.challenge.address_finder.controller;

import com.f1rst.challenge.address_finder.repository.AddressRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
@TestPropertySource(properties = {
        "client.viacep.url=http://localhost:8089"
})
public class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    void shouldSearchAddressAndSaveLogWhenZipCodeExists() throws Exception {
        addressRepository.deleteAll();

        WireMock.stubFor(get("/ws/13458870/json/")
                .willReturn(WireMock.okJson("""
                        {
                          "cep": "13458-870",
                          "logradouro": "Estrada do Barreirinho",
                          "complemento": "até 1750 - lado par",
                          "unidade": "",
                          "bairro": "Residencial Mac Knight",
                          "localidade": "Santa Bárbara D'Oeste",
                          "uf": "SP",
                          "estado": "São Paulo",
                          "regiao": "Sudeste",
                          "ibge": "3545803",
                          "gia": "6063",
                          "ddd": "19",
                          "siafi": "7017"
                        }
                        """)));

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zipCode": "13458870"
                                }
                                """))
                .andExpect(status().isNoContent());

        assertEquals(1, addressRepository.count());
    }

    @Test
    void shouldReturnNotFoundWhenZipCodeDoesNotExist() throws Exception {
        addressRepository.deleteAll();

        WireMock.stubFor(get("/ws/10101101/json/")
                .willReturn(WireMock.okJson("""
                        {
                          "erro": "true"
                        }
                        """)));

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zipCode": "10101101"
                                }
                                """))
                .andExpect(status().isNotFound());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeIsTooLong() throws Exception {
        addressRepository.deleteAll();

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zipCode": "13458870000"
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeHasLetters() throws Exception {
        addressRepository.deleteAll();

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zipCode": "aaaaaaaa"
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeIsMissing() throws Exception {
        addressRepository.deleteAll();

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadGatewayWhenExternalApiFails() throws Exception {
        addressRepository.deleteAll();

        WireMock.stubFor(get("/ws/13458870/json/")
                .willReturn(WireMock.serverError()));

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "zipCode": "13458870"
                                }
                                """))
                .andExpect(status().isBadGateway());

        assertEquals(0, addressRepository.count());
    }
    @Test
    void shouldReturnBadRequestWhenZipCodeIsTooShort() throws Exception {
        addressRepository.deleteAll();

        mockMvc.perform(post("/address/zip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "zipCode": "13458"
                            }
                            """))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }
}