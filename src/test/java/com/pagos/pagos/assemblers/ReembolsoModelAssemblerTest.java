package com.pagos.pagos.assemblers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import com.pagos.pagos.model.ReembolsoModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Ensamblador de Modelo de Reembolso")
public class ReembolsoModelAssemblerTest {

    @InjectMocks
    private ReembolsoModelAssembler assembler;

    private ReembolsoModel reembolso;
    private UUID reembolsoId;
    private UUID pagoId;

    @BeforeEach
    void setUp() {
        reembolsoId = UUID.randomUUID();
        pagoId = UUID.randomUUID();
        reembolso = new ReembolsoModel();
        reembolso.setId(reembolsoId);
        reembolso.setPagoId(pagoId);
        reembolso.setMonto(new BigDecimal("200.00"));
        reembolso.setEstado("Pendiente");
        reembolso.setFechaSolicitud(LocalDateTime.now());
        reembolso.setMotivo("Error en el pago");
    }

    @Test
    @DisplayName("Debería crear EntityModel correctamente")
    public void testToModel() {
        EntityModel<ReembolsoModel> entityModel = assembler.toModel(reembolso);
        assertNotNull(entityModel);
        assertEquals(reembolso, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
        assertFalse(entityModel.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Debería contener enlace self")
    public void testToModel_ContainsSelfLink() {
        EntityModel<ReembolsoModel> entityModel = assembler.toModel(reembolso);
        assertTrue(entityModel.hasLink("self"));
        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains(reembolsoId.toString()));
    }

    @Test
    @DisplayName("Debería contener enlace a reembolsos")
    public void testToModel_ContainsReembolsosLink() {
        EntityModel<ReembolsoModel> entityModel = assembler.toModel(reembolso);
        assertTrue(entityModel.hasLink("reembolsos"));
        Link reembolsosLink = entityModel.getLink("reembolsos").orElse(null);
        assertNotNull(reembolsosLink);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el reembolso es null")
    public void testToModel_WithNullReembolso() {
        assertThrows(IllegalArgumentException.class, () -> assembler.toModel(null));
    }

    @Test
    @DisplayName("Debería mantener todos los datos del reembolso")
    public void testToModel_PreservesAllData() {
        EntityModel<ReembolsoModel> entityModel = assembler.toModel(reembolso);
        ReembolsoModel contenido = entityModel.getContent();
        assertEquals(reembolsoId, contenido.getId());
        assertEquals(pagoId, contenido.getPagoId());
        assertEquals(new BigDecimal("200.00"), contenido.getMonto());
        assertEquals("Pendiente", contenido.getEstado());
        assertEquals("Error en el pago", contenido.getMotivo());
    }
} 