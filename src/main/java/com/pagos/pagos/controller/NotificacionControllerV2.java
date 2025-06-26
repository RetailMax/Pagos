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

@RestController
@RequestMapping(value = "/api/v2/notificaciones", produces = MediaTypes.HAL_JSON_VALUE)
public class NotificacionControllerV2 {

    private final NotificacionService notificacionService;
    private final NotificacionModelAssembler assembler;

    @Autowired
    public NotificacionControllerV2(NotificacionService notificacionService, NotificacionModelAssembler assembler) {
        this.notificacionService = notificacionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<NotificacionModel>> getAllNotificaciones() {
        List<EntityModel<NotificacionModel>> notificaciones = notificacionService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionControllerV2.class).getAllNotificaciones()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<NotificacionModel>> getNotificacionById(@PathVariable UUID id) {
        return notificacionService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<NotificacionModel>> createNotificacion(@RequestBody NotificacionModel notificacion) {
        NotificacionModel nueva = notificacionService.save(notificacion);
        return ResponseEntity
                .created(linkTo(methodOn(NotificacionControllerV2.class).getNotificacionById(nueva.getId())).toUri())
                .body(assembler.toModel(nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<NotificacionModel>> updateNotificacion(@PathVariable UUID id, @RequestBody NotificacionModel notificacion) {
        notificacion.setId(id);
        NotificacionModel actualizada = notificacionService.save(notificacion);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotificacion(@PathVariable UUID id) {
        notificacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
