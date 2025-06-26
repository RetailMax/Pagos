package com.pagos.pagos.model;

import java.math.BigDecimal;
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
@Table(name = "pagos")



public class PagoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
     @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "transaccion_id", nullable = false)
    private UUID transaccionId;

}