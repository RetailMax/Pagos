package com.pagos.pagos.controller;

import com.pagos.pagos.assemblers.PagoModelAssembler;
import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.services.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/pagos")
public class PagoControllerV2 {

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PagoModelAssembler assembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoModel>> getAllPagos() {
        List<EntityModel<PagoModel>> pagos = pagoService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(pagos,
                linkTo(methodOn(PagoControllerV2.class).getAllPagos())
                        .withSelfRel()
                        .withHref("/api/v2/pagos"));
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PagoModel> getPagosByCodigo(@PathVariable UUID id) {
        PagoModel pago = pagoService.obtenerPagoPorId(id);
        return EntityModel.of(pago,
                linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(id))
                        .withSelfRel()
                        .withHref("/api/v2/pagos/" + id));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PagoModel>> createPago(@RequestBody PagoModel pago) {
        PagoModel newPago = pagoService.save(pago);
        return ResponseEntity
                .created(linkTo(methodOn(PagoControllerV2.class).getPagosByCodigo(newPago.getId()))
                        .withSelfRel()
                        .withHref("/api/v2/pagos/" + newPago.getId())
                        .toUri())
                .body(assembler.toModel(newPago));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<PagoModel>> updatePago(@PathVariable UUID id, @RequestBody PagoModel pago) {
        pago.setId(id);
        PagoModel updatedPago = pagoService.save(pago);
        return ResponseEntity.ok(assembler.toModel(updatedPago));
    }

    @DeleteMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<?> deletePago(@PathVariable UUID id) {
        pagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
