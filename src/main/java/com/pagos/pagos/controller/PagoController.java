package com.pagos.pagos.controller;

import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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
