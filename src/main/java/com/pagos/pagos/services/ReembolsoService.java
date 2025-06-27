package com.pagos.pagos.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.repository.ReembolsoRepository;

@Service
public class ReembolsoService {

    private final ReembolsoRepository reembolsoRepository;
    private final WebpayApiService webpayApiService;

    @Autowired
    public ReembolsoService(ReembolsoRepository reembolsoRepository,
                            WebpayApiService webpayApiService) {
        this.reembolsoRepository = reembolsoRepository;
        this.webpayApiService = webpayApiService;
    }

    public ReembolsoModel procesarReembolso(UUID pagoId, BigDecimal monto) {
        // Simulamos el reembolso vía Webpay
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
}