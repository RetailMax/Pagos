package com.pagos.pagos.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagos.pagos.assemblers.PagoModelAssembler;
import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.services.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/pagos")
@Tag(name = "Pagos", description = "Metodos para gestión de pagos")
public class PagoControllerV2 {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener todos los pagos",
        description = "Retorna una lista de todos los pagos registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = PagoModel.class)))
    })
    public CollectionModel<EntityModel<PagoModel>> getAllPagos() {
        List<EntityModel<PagoModel>> pagos = pagoService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pagos,
                linkTo(methodOn(PagoControllerV2.class).getAllPagos())
                        .withSelfRel()
                        .withHref("/api/v2/pagos"));
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener pago por ID",
        description = "Retorna un pago específico basado en su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = PagoModel.class))),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<EntityModel<PagoModel>> getPagosByCodigo(
            @Parameter(description = "ID único del pago", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        PagoModel pago = pagoService.obtenerPagoPorId(id);
        if (pago == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assembler.toModel(pago));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Crear nuevo pago",
        description = "Crea un nuevo pago en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago creado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = PagoModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de pago inválidos")
    })
    public ResponseEntity<EntityModel<PagoModel>> createPago(
            @Parameter(description = "Datos del pago a crear")
            @RequestBody PagoModel pago) {
        PagoModel newPago = pagoService.save(pago);
        return ResponseEntity
                .created(linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(newPago.getId()))
                        .withSelfRel()
                        .withHref("/api/v2/pagos/" + newPago.getId())
                        .toUri())
                .body(assembler.toModel(newPago));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Actualizar pago existente",
        description = "Actualiza los datos de un pago existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = PagoModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de pago inválidos"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<EntityModel<PagoModel>> updatePago(
            @Parameter(description = "ID único del pago a actualizar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Datos actualizados del pago")
            @RequestBody PagoModel pago) {
        pago.setId(id);
        PagoModel updatedPago = pagoService.save(pago);
        return ResponseEntity.ok(assembler.toModel(updatedPago));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Eliminar pago",
        description = "Elimina un pago del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pago eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<?> deletePago(
            @Parameter(description = "ID único del pago a eliminar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        pagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
