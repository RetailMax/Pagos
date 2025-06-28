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

import com.pagos.pagos.model.PagoModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Ensamblador de Modelo de Pago")
public class PagoModelAssemblerTest {

    @InjectMocks
    private PagoModelAssembler assembler;

    private PagoModel pago;
    private UUID pagoId;
    private UUID orderId;
    private UUID usuarioId;
    private UUID transaccionId;

    @BeforeEach
    void setUp() {
        pagoId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        transaccionId = UUID.randomUUID();

        pago = new PagoModel();
        pago.setId(pagoId);
        pago.setOrderId(orderId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(new BigDecimal("50000.00"));
        pago.setEstado("Aprobado");
        pago.setFechaPago(LocalDateTime.now());
        pago.setTransaccionId(transaccionId);
    }

    @Test
    @DisplayName("Debería crear EntityModel correctamente")
    public void testToModel() {
        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel);
        assertEquals(pago, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
        assertFalse(entityModel.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Debería contener enlace self")
    public void testToModel_ContainsSelfLink() {
        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertTrue(entityModel.hasLink("self"));
        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains(pagoId.toString()));
    }

    @Test
    @DisplayName("Debería contener enlace a pagos")
    public void testToModel_ContainsPagosLink() {
        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertTrue(entityModel.hasLink("pagos"));
        Link pagosLink = entityModel.getLink("pagos").orElse(null);
        assertNotNull(pagosLink);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el pago es null")
    public void testToModel_WithNullPago() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(null);
        });
    }

    @Test
    @DisplayName("Debería manejar pago con ID null")
    public void testToModel_WithNullId() {
        // Given
        pago.setId(null);

        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel);
        assertEquals(pago, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
    }

    @Test
    @DisplayName("Debería manejar pago con datos mínimos")
    public void testToModel_WithMinimalData() {
        // Given
        PagoModel pagoMinimo = new PagoModel();
        pagoMinimo.setId(pagoId);

        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pagoMinimo);

        // Then
        assertNotNull(entityModel);
        assertEquals(pagoMinimo, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
    }

    @Test
    @DisplayName("Debería mantener todos los datos del pago")
    public void testToModel_PreservesAllData() {
        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        PagoModel contenido = entityModel.getContent();
        assertEquals(pagoId, contenido.getId());
        assertEquals(orderId, contenido.getOrderId());
        assertEquals(usuarioId, contenido.getUsuarioId());
        assertEquals(new BigDecimal("50000.00"), contenido.getMonto());
        assertEquals("Aprobado", contenido.getEstado());
        assertEquals(transaccionId, contenido.getTransaccionId());
    }

    @Test
    @DisplayName("Debería generar enlaces válidos")
    public void testToModel_ValidLinks() {
        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel.getLinks());
        assertTrue(entityModel.getLinks().toList().size() >= 2);
        
        // Verificar que todos los enlaces tienen href válidos
        entityModel.getLinks().forEach(link -> {
            assertNotNull(link.getHref());
            assertFalse(link.getHref().isEmpty());
        });
    }

    @Test
    @DisplayName("Debería manejar múltiples llamadas")
    public void testToModel_MultipleCalls() {
        // When
        EntityModel<PagoModel> entityModel1 = assembler.toModel(pago);
        EntityModel<PagoModel> entityModel2 = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel1);
        assertNotNull(entityModel2);
        assertEquals(entityModel1.getContent(), entityModel2.getContent());
    }

    @Test
    @DisplayName("Debería manejar pago con estado rechazado")
    public void testToModel_WithRejectedStatus() {
        // Given
        pago.setEstado("Rechazado");

        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel);
        assertEquals("Rechazado", entityModel.getContent().getEstado());
        assertNotNull(entityModel.getLinks());
    }

    @Test
    @DisplayName("Debería manejar pago con monto cero")
    public void testToModel_WithZeroAmount() {
        // Given
        pago.setMonto(BigDecimal.ZERO);

        // When
        EntityModel<PagoModel> entityModel = assembler.toModel(pago);

        // Then
        assertNotNull(entityModel);
        assertEquals(BigDecimal.ZERO, entityModel.getContent().getMonto());
        assertNotNull(entityModel.getLinks());
    }
} 