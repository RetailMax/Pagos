package com.pagos.pagos.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagos.pagos.model.NotificacionModel;

public interface NotificacionRepository extends JpaRepository<NotificacionModel, UUID> {

}
