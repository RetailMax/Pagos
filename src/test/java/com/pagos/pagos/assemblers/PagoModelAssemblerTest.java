package com.pagos.pagos.assemblers;

import com.pagos.pagos.model.PagoModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PagoModelAssemblerTest {

    @InjectMocks
    private PagoModelAssembler assembler;

    private PagoModel pago;

    @BeforeEach
    void setUp() {
        pago = new PagoModel();
        pago.setId(UUID.randomUUID());
        pago.setMonto(BigDecimal.valueOf(5000));
        pago.setEstado("Aprobado");
        pago.setFechaPago(LocalDateTime.now());
        pago.setOrderId(UUID.randomUUID());
        pago.setUsuarioId(UUID.randomUUID());
        pago.setTransaccionId(UUID.randomUUID());
    }

    @Test
    void testToModel() {
        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertFalse(result.getLinks().isEmpty());
        
        // Verificar que tiene el link self
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
        
        // Verificar que los links tienen las URLs correctas
        Link selfLink = result.getLinks().getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains("/api/v2/pagos/"));
        assertTrue(selfLink.getHref().contains(pago.getId().toString()));
        
        Link pagosLink = result.getLinks().getLink("pagos").orElse(null);
        assertNotNull(pagosLink);
        assertTrue(pagosLink.getHref().contains("/api/v2/pagos"));
    }

    @Test
    void testToModelWithNullPago() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            assembler.toModel(null);
        });
    }

    @Test
    void testToModelWithPagoWithoutId() {
        // Arrange
        pago.setId(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutMonto() {
        // Arrange
        pago.setMonto(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutEstado() {
        // Arrange
        pago.setEstado(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutFechaPago() {
        // Arrange
        pago.setFechaPago(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutOrderId() {
        // Arrange
        pago.setOrderId(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutUsuarioId() {
        // Arrange
        pago.setUsuarioId(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithoutTransaccionId() {
        // Arrange
        pago.setTransaccionId(null);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertNotNull(result.getLinks());
        assertTrue(result.getLinks().hasLink("self"));
        assertTrue(result.getLinks().hasLink("pagos"));
    }

    @Test
    void testToModelWithPagoWithZeroMonto() {
        // Arrange
        pago.setMonto(BigDecimal.ZERO);

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertEquals(BigDecimal.ZERO, result.getContent().getMonto());
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelWithPagoWithNegativeMonto() {
        // Arrange
        pago.setMonto(BigDecimal.valueOf(-1000));

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertEquals(BigDecimal.valueOf(-1000), result.getContent().getMonto());
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelWithPagoWithLargeMonto() {
        // Arrange
        pago.setMonto(BigDecimal.valueOf(999999.99));

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertEquals(BigDecimal.valueOf(999999.99), result.getContent().getMonto());
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelWithPagoWithEmptyEstado() {
        // Arrange
        pago.setEstado("");

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertEquals("", result.getContent().getEstado());
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelWithPagoWithDifferentEstados() {
        // Arrange
        String[] estados = {"Pendiente", "Aprobado", "Rechazado", "Cancelado", "Procesando"};
        
        for (String estado : estados) {
            pago.setEstado(estado);
            
            // Act
            EntityModel<PagoModel> result = assembler.toModel(pago);
            
            // Assert
            assertNotNull(result);
            assertEquals(pago, result.getContent());
            assertEquals(estado, result.getContent().getEstado());
            assertNotNull(result.getLinks());
        }
    }

    @Test
    void testToModelWithPagoWithPastDate() {
        // Arrange
        pago.setFechaPago(LocalDateTime.now().minusDays(1));

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertTrue(result.getContent().getFechaPago().isBefore(LocalDateTime.now()));
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelWithPagoWithFutureDate() {
        // Arrange
        pago.setFechaPago(LocalDateTime.now().plusDays(1));

        // Act
        EntityModel<PagoModel> result = assembler.toModel(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result.getContent());
        assertTrue(result.getContent().getFechaPago().isAfter(LocalDateTime.now()));
        assertNotNull(result.getLinks());
    }

    @Test
    void testToModelMultipleTimes() {
        // Act
        EntityModel<PagoModel> result1 = assembler.toModel(pago);
        EntityModel<PagoModel> result2 = assembler.toModel(pago);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(pago, result1.getContent());
        assertEquals(pago, result2.getContent());
        assertNotNull(result1.getLinks());
        assertNotNull(result2.getLinks());
        
        // Los resultados deberían ser iguales para el mismo pago
        assertEquals(result1.getContent().getId(), result2.getContent().getId());
        assertEquals(result1.getContent().getMonto(), result2.getContent().getMonto());
        assertEquals(result1.getContent().getEstado(), result2.getContent().getEstado());
    }

    @Test
    void testToModelWithDifferentPagos() {
        // Arrange
        PagoModel pago2 = new PagoModel();
        pago2.setId(UUID.randomUUID());
        pago2.setMonto(BigDecimal.valueOf(3000));
        pago2.setEstado("Rechazado");
        pago2.setFechaPago(LocalDateTime.now());
        pago2.setOrderId(UUID.randomUUID());
        pago2.setUsuarioId(UUID.randomUUID());
        pago2.setTransaccionId(UUID.randomUUID());

        // Act
        EntityModel<PagoModel> result1 = assembler.toModel(pago);
        EntityModel<PagoModel> result2 = assembler.toModel(pago2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(pago, result1.getContent());
        assertEquals(pago2, result2.getContent());
        assertNotEquals(result1.getContent().getId(), result2.getContent().getId());
        assertNotEquals(result1.getContent().getMonto(), result2.getContent().getMonto());
        assertNotEquals(result1.getContent().getEstado(), result2.getContent().getEstado());
    }
} 