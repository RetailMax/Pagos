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

import com.pagos.pagos.assemblers.NotificacionModelAssembler;
import com.pagos.pagos.model.NotificacionModel;
import com.pagos.pagos.services.NotificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/notificaciones")
@Tag(name = "Notificaciones", description = "API para gestión de notificaciones")
public class NotificacionControllerV2 {

    private final NotificacionService notificacionService;
    private final NotificacionModelAssembler assembler;

    @Autowired
    public NotificacionControllerV2(NotificacionService notificacionService, NotificacionModelAssembler assembler) {
        this.notificacionService = notificacionService;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener todas las notificaciones",
        description = "Retorna una lista de todas las notificaciones registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = NotificacionModel.class)))
    })
    public CollectionModel<EntityModel<NotificacionModel>> getAllNotificaciones() {
        List<EntityModel<NotificacionModel>> notificaciones = notificacionService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionControllerV2.class).getAllNotificaciones()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener notificación por ID",
        description = "Retorna una notificación específica basada en su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación encontrada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = NotificacionModel.class))),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<EntityModel<NotificacionModel>> getNotificacionById(
            @Parameter(description = "ID único de la notificación", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return notificacionService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Crear nueva notificación",
        description = "Crea una nueva notificación en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = NotificacionModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de notificación inválidos")
    })
    public ResponseEntity<EntityModel<NotificacionModel>> createNotificacion(
            @Parameter(description = "Datos de la notificación a crear")
            @RequestBody NotificacionModel notificacion) {
        NotificacionModel nueva = notificacionService.save(notificacion);
        return ResponseEntity
                .created(linkTo(methodOn(NotificacionControllerV2.class).getNotificacionById(nueva.getId())).toUri())
                .body(assembler.toModel(nueva));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Actualizar notificación existente",
        description = "Actualiza los datos de una notificación existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificación actualizada exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = NotificacionModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de notificación inválidos"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<EntityModel<NotificacionModel>> updateNotificacion(
            @Parameter(description = "ID único de la notificación a actualizar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Datos actualizados de la notificación")
            @RequestBody NotificacionModel notificacion) {
        notificacion.setId(id);
        NotificacionModel actualizada = notificacionService.save(notificacion);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Eliminar notificación",
        description = "Elimina una notificación del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<?> deleteNotificacion(
            @Parameter(description = "ID único de la notificación a eliminar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        notificacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

