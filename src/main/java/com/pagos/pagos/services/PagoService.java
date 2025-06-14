package com.pagos.pagos.services;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        TransaccionModel transaccion = new TransaccionModel();
        transaccion.setEstado("PENDIENTE");
        transaccion.setProveedor("WEBPAY");
        transaccion = transaccionRepository.save(transaccion);
        
        PagoModel pago = new PagoModel();
        pago.setOrderId(orderId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(monto);
        pago.setEstado("PROCESANDO");
        pago.setFechaPago(LocalDateTime.now());
        pago.setTransaccion (transaccion);
        
        return pagoRepository.save(pago);
    }

    public List<PagoModel> findAll() {
        return pagoRepository.findAll();
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

