package com.f1rst.challenge.address_finder.controller;

import com.f1rst.challenge.address_finder.repository.AddressRepository;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
@TestPropertySource(properties = {
        "client.viacep.url=http://localhost:8089",
        "coverage.allowed-states=SP"
})
public class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        WireMock.reset();
    }

    @Test
    void shouldSearchAddressInExternalApiSaveAndReturnWhenZipCodeExists() throws Exception {
        WireMock.stubFor(WireMock.get("/ws/13458870/json/")
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

        mockMvc.perform(get("/address/zip/{zipCode}", "13458870"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zipCode").value("13458870"))
                .andExpect(jsonPath("$.street").value("Estrada do Barreirinho"))
                .andExpect(jsonPath("$.complement").value("até 1750 - lado par"))
                .andExpect(jsonPath("$.neighborhood").value("Residencial Mac Knight"))
                .andExpect(jsonPath("$.city").value("Santa Bárbara D'Oeste"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.covered").value(true))
                .andExpect(jsonPath("$.source").value("EXTERNAL_API"));

        assertEquals(1, addressRepository.count());

        WireMock.verify(1, getRequestedFor(urlEqualTo("/ws/13458870/json/")));
    }

    @Test
    void shouldReturnAddressFromDatabaseWhenZipCodeAlreadyExists() throws Exception {
        WireMock.stubFor(WireMock.get("/ws/13458870/json/")
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

        mockMvc.perform(get("/address/zip/{zipCode}", "13458870"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("EXTERNAL_API"));

        mockMvc.perform(get("/address/zip/{zipCode}", "13458870"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zipCode").value("13458870"))
                .andExpect(jsonPath("$.street").value("Estrada do Barreirinho"))
                .andExpect(jsonPath("$.city").value("Santa Bárbara D'Oeste"))
                .andExpect(jsonPath("$.state").value("SP"))
                .andExpect(jsonPath("$.covered").value(true))
                .andExpect(jsonPath("$.source").value("DATABASE"));

        assertEquals(2, addressRepository.count());

        WireMock.verify(1, getRequestedFor(urlEqualTo("/ws/13458870/json/")));
    }

    @Test
    void shouldReturnNotCoveredWhenStateIsNotConfiguredAsAllowed() throws Exception {
        WireMock.stubFor(WireMock.get("/ws/20040020/json/")
                .willReturn(WireMock.okJson("""
                        {
                          "cep": "20040-020",
                          "logradouro": "Rua da Quitanda",
                          "complemento": "",
                          "unidade": "",
                          "bairro": "Centro",
                          "localidade": "Rio de Janeiro",
                          "uf": "RJ",
                          "estado": "Rio de Janeiro",
                          "regiao": "Sudeste",
                          "ibge": "3304557",
                          "gia": "",
                          "ddd": "21",
                          "siafi": "6001"
                        }
                        """)));

        mockMvc.perform(get("/address/zip/{zipCode}", "20040020"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zipCode").value("20040020"))
                .andExpect(jsonPath("$.city").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.state").value("RJ"))
                .andExpect(jsonPath("$.covered").value(false))
                .andExpect(jsonPath("$.source").value("EXTERNAL_API"));

        assertEquals(1, addressRepository.count());
    }

    @Test
    void shouldReturnNotFoundWhenZipCodeDoesNotExist() throws Exception {
        WireMock.stubFor(WireMock.get("/ws/10101101/json/")
                .willReturn(WireMock.okJson("""
                        {
                          "erro": "true"
                        }
                        """)));

        mockMvc.perform(get("/address/zip/{zipCode}", "10101101"))
                .andExpect(status().isNotFound());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeIsTooLong() throws Exception {
        mockMvc.perform(get("/address/zip/{zipCode}", "13458870000"))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeHasLetters() throws Exception {
        mockMvc.perform(get("/address/zip/{zipCode}", "aaaaaaaa"))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadRequestWhenZipCodeIsTooShort() throws Exception {
        mockMvc.perform(get("/address/zip/{zipCode}", "13458"))
                .andExpect(status().isBadRequest());

        assertEquals(0, addressRepository.count());
    }

    @Test
    void shouldReturnBadGatewayWhenExternalApiFails() throws Exception {
        WireMock.stubFor(WireMock.get("/ws/13458870/json/")
                .willReturn(WireMock.serverError()));

        mockMvc.perform(get("/address/zip/{zipCode}", "13458870"))
                .andExpect(status().isBadGateway());

        assertEquals(0, addressRepository.count());
    }
}