package com.pagos.pagos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.model.TransaccionModel;

@Service
public class WebpayApiService {

    public TransaccionModel procesarTransaccion(UUID orderId, BigDecimal monto) {
        TransaccionModel transaccion = new TransaccionModel();
        transaccion.setId(UUID.randomUUID());
        transaccion.setEstado("APROBADO");
        transaccion.setProveedor("WEBPAYPLUS");
        transaccion.setDetalleError(null);
        transaccion.setMonto(monto);
        transaccion.setFechaTransaccion(LocalDateTime.now());

        return transaccion;
    }

    public ReembolsoModel solicitarReembolso(UUID pagoId, BigDecimal monto) {
        ReembolsoModel reembolso = new ReembolsoModel();
        reembolso.setId(UUID.randomUUID());
        reembolso.setPagoId(pagoId);
        reembolso.setFechaSolicitud(LocalDateTime.now());
        reembolso.setEstado("PENDIENTE"); 

        return reembolso;
    }

    public TransaccionModel consultarEstadoTransaccion(UUID transaccionId) {
        TransaccionModel transaccion = new TransaccionModel();
        transaccion.setId(transaccionId);
        transaccion.setEstado("APROBADO");
        transaccion.setProveedor("WEBPAYPLUS");
        transaccion.setDetalleError(null);
        transaccion.setFechaTransaccion(LocalDateTime.now().minusMinutes(5));
        return transaccion;
    }
}
