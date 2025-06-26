package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.NotificacionControllerV2;
import com.pagos.pagos.model.NotificacionModel;

@Component
public class NotificacionModelAssembler implements RepresentationModelAssembler<NotificacionModel, EntityModel<NotificacionModel>> {

    @Override
    public EntityModel<NotificacionModel> toModel(NotificacionModel notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException("La notificación no puede ser null");
        }

        return EntityModel.of(notificacion,
                linkTo(methodOn(NotificacionControllerV2.class).getNotificacionById(notificacion.getId())).withSelfRel(),
                linkTo(methodOn(NotificacionControllerV2.class).getAllNotificaciones()).withRel("notificaciones")
        );
    }
}
