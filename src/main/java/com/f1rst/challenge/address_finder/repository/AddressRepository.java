package com.f1rst.challenge.address_finder.repository;

import com.f1rst.challenge.address_finder.repository.entity.AddressQueryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<AddressQueryLogEntity, Long> {

    Optional<AddressQueryLogEntity> findFirstByCepOrderBySearchedAtDesc(String zipCode);
}
