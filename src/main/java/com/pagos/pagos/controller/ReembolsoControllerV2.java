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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/reembolsos")
@Tag(name = "Reembolsos", description = "API para gestión de reembolsos")
public class ReembolsoControllerV2 {

    private final ReembolsoService reembolsoService;

    @Autowired
    public ReembolsoControllerV2(ReembolsoService reembolsoService) {
        this.reembolsoService = reembolsoService;
    }

    // Crear un reembolso
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Crear nuevo reembolso",
        description = "Crea un nuevo reembolso para un pago específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reembolso creado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = ReembolsoModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de reembolso inválidos"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ReembolsoModel crearReembolso(
            @Parameter(description = "ID del pago a reembolsar", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID pagoId,
            @Parameter(description = "Monto del reembolso", example = "15000.50")
            @RequestParam BigDecimal monto
    ) {
        return reembolsoService.procesarReembolso(pagoId, monto);
    }

    // Obtiene una lista de todos los reembolsos
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener todos los reembolsos",
        description = "Retorna una lista de todos los reembolsos registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de reembolsos obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = ReembolsoModel.class)))
    })
    public List<ReembolsoModel> getAllReembolsos() {
        return reembolsoService.obtenerTodos();
    }

    // Obtiene un reembolso por su ID
    @GetMapping(value = "/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener reembolso por ID",
        description = "Retorna un reembolso específico basado en su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reembolso encontrado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = ReembolsoModel.class))),
        @ApiResponse(responseCode = "404", description = "Reembolso no encontrado")
    })
    public ReembolsoModel getReembolsoById(
            @Parameter(description = "ID único del reembolso", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return reembolsoService.obtenerPorId(id);
    }

    // Eliminar un reembolso por su ID
    @DeleteMapping(value = "/{id}",produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Eliminar reembolso",
        description = "Elimina un reembolso del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reembolso eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reembolso no encontrado")
    })
    public void eliminarPorId(
            @Parameter(description = "ID único del reembolso a eliminar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        reembolsoService.eliminarPorId(id);
    }
}
