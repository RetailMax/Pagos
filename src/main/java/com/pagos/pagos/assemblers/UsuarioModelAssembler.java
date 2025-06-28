package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.NotificacionControllerV2;
import com.pagos.pagos.controller.PagoControllerV2;
import com.pagos.pagos.controller.UsuarioControllerV2;
import com.pagos.pagos.model.UsuarioModel;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioModel, EntityModel<UsuarioModel>> {

    @Override
    public EntityModel<UsuarioModel> toModel(UsuarioModel usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }

        return EntityModel.of(usuario,
                // Links básicos
                linkTo(methodOn(UsuarioControllerV2.class).getUsuarioById(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioControllerV2.class).getAllUsuarios()).withRel("usuarios"),
                
                // Links de acciones
                linkTo(methodOn(UsuarioControllerV2.class).updateUsuario(usuario.getId(), usuario)).withRel("update"),
                linkTo(methodOn(UsuarioControllerV2.class).deleteUsuario(usuario.getId())).withRel("delete"),
                
                // Links de navegación a recursos relacionados
                linkTo(methodOn(PagoControllerV2.class).getAllPagos()).withRel("pagos"),
                linkTo(methodOn(NotificacionControllerV2.class).getAllNotificaciones()).withRel("notificaciones")
        );
    }
}