package com.pagos.pagos.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagos.pagos.model.PagoModel;

@Repository
public interface PagoRepository extends JpaRepository<PagoModel, UUID> {
}

