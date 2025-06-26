package com.pagos.pagos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagos.pagos.model.ReembolsoModel;

public interface ReembolsoRepository extends JpaRepository<ReembolsoModel, UUID> {
}