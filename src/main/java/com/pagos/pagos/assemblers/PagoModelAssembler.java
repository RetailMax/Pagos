package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.PagoControllerV2;
import com.pagos.pagos.model.PagoModel;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoModel, EntityModel<PagoModel>> {

    @Override
    public EntityModel<PagoModel> toModel(PagoModel pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser null");
        }

        return EntityModel.of(pago,
                linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).getAllPagos()).withRel("pagos")
        );
    }
}