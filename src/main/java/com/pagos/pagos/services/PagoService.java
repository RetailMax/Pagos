package com.pagos.pagos.services;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.repository.PagoRepository;
import com.pagos.pagos.repository.TransaccionRepository;

@Service

public class PagoService {
private final PagoRepository pagoRepository;
    private final TransaccionRepository transaccionRepository;
    
    @Autowired
    public PagoService(PagoRepository pagoRepository, TransaccionRepository transaccionRepository) {
        this.pagoRepository = pagoRepository;
        this.transaccionRepository = transaccionRepository;
    }
    

    public PagoModel procesarPago(UUID orderId, UUID usuarioId, BigDecimal monto) {
        // Esto crea y guarda un pago sin haber realizado la transacción
        PagoModel pago = new PagoModel();
        pago.setOrderId(orderId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(monto);
        pago.setEstado("PROCESANDO");
        pago.setFechaPago(LocalDateTime.now());

        PagoModel pagoGuardado = pagoRepository.save(pago);

        // Crear la transacción referenciando al pago por medio 
        TransaccionModel transaccion = new TransaccionModel();
        transaccion.setPagoId(pagoGuardado.getId());
        transaccion.setEstado("PENDIENTE");
        transaccion.setProveedor("WEBPAY");
        transaccion.setFechaTransaccion(LocalDateTime.now());
        transaccion.setMonto(monto);

        TransaccionModel transaccionGuardada = transaccionRepository.save(transaccion);

        //Actualizar el pago con el ID de la transacción
        pagoGuardado.setTransaccionId(transaccionGuardada.getId());
        return pagoRepository.save(pagoGuardado);
    }

    public List<PagoModel> findAll() {
        return pagoRepository.findAll();
    }
    
    public Optional<PagoModel> findById(UUID id) {
        return pagoRepository.findById(id);
    }

    public PagoModel save(PagoModel pago) {
        return pagoRepository.save(pago);
    }

    public void deleteById(UUID id) {
        pagoRepository.deleteById(id);
    }
    public PagoModel obtenerPagoPorId(UUID id) {
        return pagoRepository.findById(id).orElse(null);
    }
    
    public void actualizarEstadoPago(UUID pagoId, String estado) {
        pagoRepository.findById(pagoId).ifPresent(pago -> {
            pago.setEstado(estado);
            pagoRepository.save(pago);
        });
    }
}

