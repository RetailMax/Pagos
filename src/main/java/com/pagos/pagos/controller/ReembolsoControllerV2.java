package com.pagos.pagos.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.services.ReembolsoService;

@RestController
@RequestMapping("/api/v2/reembolsos")
public class ReembolsoControllerV2 {

    private final ReembolsoService reembolsoService;

    @Autowired
    public ReembolsoControllerV2(ReembolsoService reembolsoService) {
        this.reembolsoService = reembolsoService;
    }

    // Crear un reembolso
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ReembolsoModel crearReembolso(
            @RequestParam UUID pagoId,
            @RequestParam BigDecimal monto
    ) {
        return reembolsoService.procesarReembolso(pagoId, monto);
    }

    // Obtiene una lista de todos los reembolsos
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public List<ReembolsoModel> getAllReembolsos() {
        return reembolsoService.obtenerTodos();
    }

    // Obtiene un reembolso por su ID
    @GetMapping(value = "/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    public ReembolsoModel getReembolsoById(@PathVariable UUID id) {
        return reembolsoService.obtenerPorId(id);
    }

    // Eliminar un reembolso por su ID
    @DeleteMapping(value = "/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    public void eliminarPorId(@PathVariable UUID id) {
        reembolsoService.eliminarPorId(id);
    }
}
