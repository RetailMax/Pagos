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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/api/v2/usuarios", produces = MediaTypes.HAL_JSON_VALUE)
@Tag(name = "Usuarios", description = "Metodos para gestión de usuarios del sistema de pagos")
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
    @Operation(
        summary = "Obtener todos los usuarios",
        description = "Retorna una lista de todos los usuarios registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UsuarioModel.class)))
    })
    public CollectionModel<EntityModel<UsuarioModel>> getAllUsuarios() {
        List<EntityModel<UsuarioModel>> usuarios = usuarioService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withSelfRel());
    }
    //Busca usuario por su uuid
    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Obtener usuario por ID",
        description = "Retorna un usuario específico basado en su UUID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UsuarioModel.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<EntityModel<UsuarioModel>> getUsuarioById(
            @Parameter(description = "ID único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return usuarioService.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //Crea usuario
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Crear nuevo usuario",
        description = "Crea un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UsuarioModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos")
    })
    public ResponseEntity<EntityModel<UsuarioModel>> createUsuario(
            @Parameter(description = "Datos del usuario a crear")
            @RequestBody UsuarioModel usuario) {
        UsuarioModel nuevo = usuarioService.save(usuario);
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioControllerV2.class).getUsuarioById(nuevo.getId())).toUri())
                .body(assembler.toModel(nuevo));
    }
    //Actualiza usuario
    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Actualizar usuario existente",
        description = "Actualiza los datos de un usuario existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UsuarioModel.class))),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos o incompletos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el ID proporcionado"),
        @ApiResponse(responseCode = "409", description = "Conflicto: Email duplicado con otro usuario")
    })
    public ResponseEntity<EntityModel<UsuarioModel>> updateUsuario(
            @Parameter(description = "ID único del usuario a actualizar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Parameter(description = "Datos actualizados del usuario")
            @RequestBody UsuarioModel usuario) {
        usuario.setId(id);
        UsuarioModel actualizado = usuarioService.save(usuario);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }
    //elimina usuario
    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(
        summary = "Eliminar usuario",
        description = "Elimina un usuario del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<?> deleteUsuario(
            @Parameter(description = "ID único del usuario a eliminar", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}