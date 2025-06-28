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

import com.pagos.pagos.assemblers.TransaccionModelAssembler;
import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.services.TransaccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v2/transacciones", produces = MediaTypes.HAL_JSON_VALUE)
@Tag(name = "Transacciones", description = "API para gestión de transacciones de pago")
public class TransaccionControllerV2 {

    private final TransaccionService transaccionService;
    private final TransaccionModelAssembler assembler;

    @Autowired
    public TransaccionControllerV2(TransaccionService transaccionService, TransaccionModelAssembler assembler) {
        this.transaccionService = transaccionService;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener todas las transacciones",
        description = "Retorna una lista de todas las transacciones registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = TransaccionModel.class)))
    })
    public CollectionModel<EntityModel<TransaccionModel>> getAllTransacciones() {
        List<EntityModel<TransaccionModel>> transacciones = transaccionService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(transacciones,
                linkTo(methodOn(TransaccionControllerV2.class).getAllTransacciones()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener transacción por ID",
        description = "Retorna una transacción específica basada en su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción encontrada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = TransaccionModel.class))),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<EntityModel<TransaccionModel>> getTransaccionById(
            @Parameter(description = "ID único de la transacción", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return transaccionService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
        summary = "Crear nueva transacción",
        description = "Crea una nueva transacción en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transacción creada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = TransaccionModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos")
    })
    public ResponseEntity<EntityModel<TransaccionModel>> createTransaccion(
            @Parameter(description = "Datos de la transacción a crear")
            @RequestBody TransaccionModel transaccion) {
        TransaccionModel nueva = transaccionService.save(transaccion);
        return ResponseEntity
                .created(linkTo(methodOn(TransaccionControllerV2.class).getTransaccionById(nueva.getId())).toUri())
                .body(assembler.toModel(nueva));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Actualizar transacción existente",
        description = "Actualiza los datos de una transacción existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transacción actualizada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = TransaccionModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de transacción inválidos"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<EntityModel<TransaccionModel>> updateTransaccion(
            @Parameter(description = "ID único de la transacción a actualizar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Datos actualizados de la transacción")
            @RequestBody TransaccionModel transaccion) {
        transaccion.setId(id);
        TransaccionModel actualizada = transaccionService.save(transaccion);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Eliminar transacción",
        description = "Elimina una transacción del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transacción eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    public ResponseEntity<?> deleteTransaccion(
            @Parameter(description = "ID único de la transacción a eliminar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        transaccionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

