package com.pagos.pagos.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.repository.ReembolsoRepository;

@Service
public class ReembolsoService {

    private static final Logger log = LoggerFactory.getLogger(ReembolsoService.class);

    private final ReembolsoRepository reembolsoRepository;
    private final WebpayApiService webpayApiService;

    @Autowired
    public ReembolsoService(ReembolsoRepository reembolsoRepository,
                            WebpayApiService webpayApiService) {
        this.reembolsoRepository = reembolsoRepository;
        this.webpayApiService = webpayApiService;
    }

    @Transactional
    public ReembolsoModel procesarReembolso(UUID pagoId, BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        
        log.info("Solicitando reembolso para pagoId={} con monto={}", pagoId, monto);

        // Aquí podrías agregar validación si el pago existe, si tienes PagoRepository inyectado

        ReembolsoModel reembolso = webpayApiService.solicitarReembolso(pagoId, monto);
        return reembolsoRepository.save(reembolso);
    }

    public List<ReembolsoModel> obtenerTodos() {
        return reembolsoRepository.findAll();
    }

    public ReembolsoModel obtenerPorId(UUID id) {
        return reembolsoRepository.findById(id).orElse(null);
    }

    public void eliminarPorId(UUID id) {
        reembolsoRepository.deleteById(id);
    }

    public void actualizarEstadoReembolso(UUID reembolsoId, String nuevoEstado) {
        reembolsoRepository.findById(reembolsoId).ifPresent(reembolso -> {
            reembolso.setEstado(nuevoEstado);
            reembolsoRepository.save(reembolso);
        });
    }
}