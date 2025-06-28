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

import com.pagos.pagos.model.TransaccionModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Ensamblador de Modelo de Transacción")
public class TransaccionModelAssemblerTest {

    @InjectMocks
    private TransaccionModelAssembler assembler;

    private TransaccionModel transaccion;
    private UUID transaccionId;
    private UUID pagoId;

    @BeforeEach
    void setUp() {
        transaccionId = UUID.randomUUID();
        pagoId = UUID.randomUUID();
        transaccion = new TransaccionModel();
        transaccion.setId(transaccionId);
        transaccion.setPagoId(pagoId);
        transaccion.setMonto(new BigDecimal("1000.00"));
        transaccion.setEstado("Exitoso");
        transaccion.setFechaTransaccion(LocalDateTime.now());
        transaccion.setProveedor("Webpay");
        transaccion.setDetalleError(null);
    }

    @Test
    @DisplayName("Debería crear EntityModel correctamente")
    public void testToModel() {
        EntityModel<TransaccionModel> entityModel = assembler.toModel(transaccion);
        assertNotNull(entityModel);
        assertEquals(transaccion, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
        assertFalse(entityModel.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Debería contener enlace self")
    public void testToModel_ContainsSelfLink() {
        EntityModel<TransaccionModel> entityModel = assembler.toModel(transaccion);
        assertTrue(entityModel.hasLink("self"));
        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains(transaccionId.toString()));
    }

    @Test
    @DisplayName("Debería contener enlace a transacciones")
    public void testToModel_ContainsTransaccionesLink() {
        EntityModel<TransaccionModel> entityModel = assembler.toModel(transaccion);
        assertTrue(entityModel.hasLink("transacciones"));
        Link transaccionesLink = entityModel.getLink("transacciones").orElse(null);
        assertNotNull(transaccionesLink);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la transacción es null")
    public void testToModel_WithNullTransaccion() {
        assertThrows(IllegalArgumentException.class, () -> assembler.toModel(null));
    }

    @Test
    @DisplayName("Debería mantener todos los datos de la transacción")
    public void testToModel_PreservesAllData() {
        EntityModel<TransaccionModel> entityModel = assembler.toModel(transaccion);
        TransaccionModel contenido = entityModel.getContent();
        assertEquals(transaccionId, contenido.getId());
        assertEquals(pagoId, contenido.getPagoId());
        assertEquals(new BigDecimal("1000.00"), contenido.getMonto());
        assertEquals("Exitoso", contenido.getEstado());
        assertEquals("Webpay", contenido.getProveedor());
    }
} 