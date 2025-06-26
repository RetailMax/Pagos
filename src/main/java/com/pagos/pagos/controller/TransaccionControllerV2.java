package com.pagos.pagos.controller;

import com.pagos.pagos.assemblers.TransaccionModelAssembler;
import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.services.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(value = "/api/v2/transacciones", produces = MediaTypes.HAL_JSON_VALUE)
public class TransaccionControllerV2 {

    private final TransaccionService transaccionService;
    private final TransaccionModelAssembler assembler;

    @Autowired
    public TransaccionControllerV2(TransaccionService transaccionService, TransaccionModelAssembler assembler) {
        this.transaccionService = transaccionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<TransaccionModel>> getAllTransacciones() {
        List<EntityModel<TransaccionModel>> transacciones = transaccionService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(transacciones,
                linkTo(methodOn(TransaccionControllerV2.class).getAllTransacciones()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TransaccionModel>> getTransaccionById(@PathVariable UUID id) {
        return transaccionService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TransaccionModel>> createTransaccion(@RequestBody TransaccionModel transaccion) {
        TransaccionModel nueva = transaccionService.save(transaccion);
        return ResponseEntity
                .created(linkTo(methodOn(TransaccionControllerV2.class).getTransaccionById(nueva.getId())).toUri())
                .body(assembler.toModel(nueva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TransaccionModel>> updateTransaccion(@PathVariable UUID id, @RequestBody TransaccionModel transaccion) {
        transaccion.setId(id);
        TransaccionModel actualizada = transaccionService.save(transaccion);
        return ResponseEntity.ok(assembler.toModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaccion(@PathVariable UUID id) {
        transaccionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

