package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.PagoControllerV2;
import com.pagos.pagos.controller.ReembolsoControllerV2;
import com.pagos.pagos.controller.TransaccionControllerV2;
import com.pagos.pagos.controller.UsuarioControllerV2;
import com.pagos.pagos.model.PagoModel;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoModel, EntityModel<PagoModel>> {

    @Override
    public EntityModel<PagoModel> toModel(PagoModel pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago no puede ser null");
        }

        EntityModel<PagoModel> pagoModel = EntityModel.of(pago,
                // Links básicos
                linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).getAllPagos()).withRel("pagos"),
                
                // Links de navegación a recursos relacionados
                linkTo(methodOn(TransaccionControllerV2.class).getTransaccionById(pago.getTransaccionId())).withRel("transaccion"),
                linkTo(methodOn(UsuarioControllerV2.class).getUsuarioById(pago.getUsuarioId())).withRel("usuario"),
                
                // Links de acciones
                linkTo(methodOn(PagoControllerV2.class).updatePago(pago.getId(), pago)).withRel("update"),
                linkTo(methodOn(PagoControllerV2.class).deletePago(pago.getId())).withRel("delete")
        );

        // Links condicionales basados en el estado del pago
        if ("Aprobado".equals(pago.getEstado())) {
            pagoModel.add(
                linkTo(methodOn(ReembolsoControllerV2.class).crearReembolso(pago.getId(), null)).withRel("reembolsar")
            );
        }

        if ("Pendiente".equals(pago.getEstado())) {
            pagoModel.add(
                linkTo(methodOn(PagoControllerV2.class).updatePago(pago.getId(), pago)).withRel("aprobar"),
                linkTo(methodOn(PagoControllerV2.class).updatePago(pago.getId(), pago)).withRel("rechazar")
            );
        }

        return pagoModel;
    }
}