package com.pagos.pagos.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notificaciones")
public class NotificacionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String tipo;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    
    @Column(name = "destinatario_id")
    private UUID destinatarioId;
}
