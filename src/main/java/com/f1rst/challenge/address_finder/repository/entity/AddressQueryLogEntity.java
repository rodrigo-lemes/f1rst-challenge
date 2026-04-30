package com.f1rst.challenge.address_finder.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "address_query_log")
@Getter
@Setter
public class AddressQueryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    @Column(name = "zip_code", nullable = false, length = 9)
    private String cep;

    @Column(name = "street", length = 255)
    private String logradouro;

    @Column(name = "complement", length = 255)
    private String complemento;

    @Column(name = "unit", length = 100)
    private String unidade;

    @Column(name = "neighborhood", length = 150)
    private String bairro;

    @Column(name = "city", length = 150)
    private String localidade;

    @Column(name = "state_code", length = 2)
    private String uf;

    @Column(name = "state", length = 100)
    private String estado;

    @Column(name = "region", length = 100)
    private String regiao;

    @Column(name = "ibge_code", length = 20)
    private String ibge;

    @Column(name = "gia_code", length = 20)
    private String gia;

    @Column(name = "area_code", length = 5)
    private String ddd;

    @Column(name = "siafi_code", length = 20)
    private String siafi;
}