package com.pagos.pagos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.pagos.pagos.controller.PagoControllerV2;
import com.pagos.pagos.controller.TransaccionControllerV2;
import com.pagos.pagos.model.TransaccionModel;

@Component
public class TransaccionModelAssembler implements RepresentationModelAssembler<TransaccionModel, EntityModel<TransaccionModel>> {

    @Override
    public EntityModel<TransaccionModel> toModel(TransaccionModel transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser null");
        }

        EntityModel<TransaccionModel> transaccionModel = EntityModel.of(transaccion,
                // Links básicos
                linkTo(methodOn(TransaccionControllerV2.class).getTransaccionById(transaccion.getId())).withSelfRel(),
                linkTo(methodOn(TransaccionControllerV2.class).getAllTransacciones()).withRel("transacciones"),
                
                // Links de navegación a recursos relacionados
                linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(transaccion.getPagoId())).withRel("pago"),
                
                // Links de acciones
                linkTo(methodOn(TransaccionControllerV2.class).updateTransaccion(transaccion.getId(), transaccion)).withRel("update"),
                linkTo(methodOn(TransaccionControllerV2.class).deleteTransaccion(transaccion.getId())).withRel("delete")
        );

        // Links condicionales basados en el estado de la transacción
        if ("Pendiente".equals(transaccion.getEstado())) {
            transaccionModel.add(
                linkTo(methodOn(TransaccionControllerV2.class).updateTransaccion(transaccion.getId(), transaccion)).withRel("aprobar"),
                linkTo(methodOn(TransaccionControllerV2.class).updateTransaccion(transaccion.getId(), transaccion)).withRel("rechazar")
            );
        }

        if ("Aprobado".equals(transaccion.getEstado())) {
            transaccionModel.add(
                linkTo(methodOn(TransaccionControllerV2.class).updateTransaccion(transaccion.getId(), transaccion)).withRel("reversar")
            );
        }

        return transaccionModel;
    }
}
