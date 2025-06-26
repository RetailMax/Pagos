package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.TransaccionControllerV2;
import com.pagos.pagos.model.TransaccionModel;

@Component
public class TransaccionModelAssembler implements RepresentationModelAssembler<TransaccionModel, EntityModel<TransaccionModel>> {

    @Override
    public EntityModel<TransaccionModel> toModel(TransaccionModel transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser null");
        }

        return EntityModel.of(transaccion,
                linkTo(methodOn(TransaccionControllerV2.class).getTransaccionById(transaccion.getId())).withSelfRel(),
                linkTo(methodOn(TransaccionControllerV2.class).getAllTransacciones()).withRel("transacciones")
        );
    }
}
