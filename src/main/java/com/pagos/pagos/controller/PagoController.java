package com.pagos.pagos.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.services.PagoService;

@RestController
@RequestMapping("/api/v1/pagos")

public class PagoController {
    private final PagoService pagoService;
    
    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }
    
    @PostMapping
    public ResponseEntity<PagoModel> procesarPago(
            @RequestParam UUID orderId,
            @RequestParam UUID usuarioId,
            @RequestParam BigDecimal monto) {
        PagoModel pago = pagoService.procesarPago(orderId, usuarioId, monto);
        return ResponseEntity.ok(pago);
    }
    @GetMapping("/testing")
    public String testing() {
        return "hola!";
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<PagoModel> obtenerPago(@PathVariable UUID id) {
        PagoModel pago = pagoService.obtenerPagoPorId(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable UUID id,
            @RequestParam String estado) {
        pagoService.actualizarEstadoPago(id, estado);
        return ResponseEntity.ok().build();
    }
}
