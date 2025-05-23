package com.pagos.pagos.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pagos.pagos.model.PagoModel;

@Service
public class WebpayService {
    public boolean iniciarTransaccion(PagoModel pago) {
        // Lógica para integrar con Webpay API
        return true;
    }
    
    public String consultarEstado(UUID transactionId) {
        return "APROBADO";
    }
    
    public boolean procesarReembolso(UUID pagoId) {
        return true;
    }
}
