package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.ReembolsoControllerV2;
import com.pagos.pagos.model.ReembolsoModel;

@Component
public class ReembolsoModelAssembler implements RepresentationModelAssembler<ReembolsoModel, EntityModel<ReembolsoModel>> {

    @Override
    public EntityModel<ReembolsoModel> toModel(ReembolsoModel reembolso) {
        if (reembolso == null) {
            throw new IllegalArgumentException("El reembolso no puede ser null");
        }

        return EntityModel.of(reembolso,
                linkTo(methodOn(ReembolsoControllerV2.class).obtenerPorId(reembolso.getId())).withSelfRel(),
                linkTo(methodOn(ReembolsoControllerV2.class).listarTodos()).withRel("reembolsos")
        );
    }
}
