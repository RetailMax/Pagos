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

import com.pagos.pagos.assemblers.UsuarioModelAssembler;
import com.pagos.pagos.model.UsuarioModel;
import com.pagos.pagos.services.UsuarioService;

@RestController
@RequestMapping(value = "/api/v2/usuarios", produces = MediaTypes.HAL_JSON_VALUE)
public class UsuarioControllerV2 {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    @Autowired
    public UsuarioControllerV2(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }
    //Lista usuarios
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioModel>> getAllUsuarios() {
        List<EntityModel<UsuarioModel>> usuarios = usuarioService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withSelfRel());
    }
    //Busca usuario por su uuid
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioModel>> getUsuarioById(@PathVariable UUID id) {
        return usuarioService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //Crea usuario
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioModel>> createUsuario(@RequestBody UsuarioModel usuario) {
        UsuarioModel nuevo = usuarioService.save(usuario);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioControllerV2.class).getUsuarioById(nuevo.getId())).toUri())
                .body(assembler.toModel(nuevo));
    }
    //Actualiza usuario
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<UsuarioModel>> updateUsuario(@PathVariable UUID id, @RequestBody UsuarioModel usuario) {
        usuario.setId(id);
        UsuarioModel actualizado = usuarioService.save(usuario);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }
    //elimina usuario
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deleteUsuario(@PathVariable UUID id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}