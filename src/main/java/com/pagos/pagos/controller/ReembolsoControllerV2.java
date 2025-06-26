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

import com.pagos.pagos.assemblers.ReembolsoModelAssembler;
import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.services.ReembolsoService;

@RestController
@RequestMapping(value = "/api/v2/reembolsos", produces = MediaTypes.HAL_JSON_VALUE)
public class ReembolsoControllerV2 {

    private final ReembolsoService reembolsoService;
    private final ReembolsoModelAssembler assembler;

    @Autowired
    public ReembolsoControllerV2(ReembolsoService reembolsoService, ReembolsoModelAssembler assembler) {
        this.reembolsoService = reembolsoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ReembolsoModel>> getAllReembolsos() {
        List<EntityModel<ReembolsoModel>> reembolsos = reembolsoService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(reembolsos,
                linkTo(methodOn(ReembolsoControllerV2.class).getAllReembolsos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReembolsoModel>> getReembolsoById(@PathVariable UUID id) {
        return reembolsoService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<ReembolsoModel>> createReembolso(@RequestBody ReembolsoModel reembolso) {
        ReembolsoModel nuevo = reembolsoService.save(reembolso);
        return ResponseEntity
                .created(linkTo(methodOn(ReembolsoControllerV2.class).getReembolsoById(nuevo.getId())).toUri())
                .body(assembler.toModel(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ReembolsoModel>> updateReembolso(@PathVariable UUID id, @RequestBody ReembolsoModel reembolso) {
        reembolso.setId(id);
        ReembolsoModel actualizado = reembolsoService.save(reembolso);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReembolso(@PathVariable UUID id) {
        reembolsoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
