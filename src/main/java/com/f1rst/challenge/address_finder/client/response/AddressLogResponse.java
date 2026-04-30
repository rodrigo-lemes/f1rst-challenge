package com.f1rst.challenge.address_finder.client.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AddressLogResponse {

    private LocalDateTime searchedAt;

    private String cep;

    private String logradouro;

    private String complemento;

    private String unidade;

    private String bairro;

    private String localidade;

    private String uf;

    private String estado;

    private String regiao;

    private String ibge;

    private String gia;

    private String ddd;

    private String siafi;

    private String erro;

}