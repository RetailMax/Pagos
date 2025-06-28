package com.pagos.pagos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.model.TransaccionModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio Webpay API")
public class WebpayApiServiceTest {

    @InjectMocks
    private WebpayApiService webpayApiService;

    private UUID orderId;
    private UUID pagoId;
    private UUID transaccionId;
    private BigDecimal monto;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        pagoId = UUID.randomUUID();
        transaccionId = UUID.randomUUID();
        monto = new BigDecimal("25000.00");
    }

    @Test
    @DisplayName("Debería procesar una transacción exitosamente")
    public void testProcesarTransaccion() {
        // When
        TransaccionModel resultado = webpayApiService.procesarTransaccion(orderId, monto);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals("WEBPAYPLUS", resultado.getProveedor());
        assertEquals(monto, resultado.getMonto());
        assertNull(resultado.getDetalleError());
        assertNotNull(resultado.getFechaTransaccion());
        assertTrue(resultado.getFechaTransaccion().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(resultado.getFechaTransaccion().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debería procesar transacción con monto cero")
    public void testProcesarTransaccion_WithZeroAmount() {
        // Given
        BigDecimal montoCero = BigDecimal.ZERO;

        // When
        TransaccionModel resultado = webpayApiService.procesarTransaccion(orderId, montoCero);

        // Then
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals(montoCero, resultado.getMonto());
    }

    @Test
    @DisplayName("Debería procesar transacción con monto negativo")
    public void testProcesarTransaccion_WithNegativeAmount() {
        // Given
        BigDecimal montoNegativo = new BigDecimal("-1000.00");

        // When
        TransaccionModel resultado = webpayApiService.procesarTransaccion(orderId, montoNegativo);

        // Then
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals(montoNegativo, resultado.getMonto());
    }

    @Test
    @DisplayName("Debería procesar transacción con monto grande")
    public void testProcesarTransaccion_WithLargeAmount() {
        // Given
        BigDecimal montoGrande = new BigDecimal("999999.99");

        // When
        TransaccionModel resultado = webpayApiService.procesarTransaccion(orderId, montoGrande);

        // Then
        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals(montoGrande, resultado.getMonto());
    }

    @Test
    @DisplayName("Debería generar IDs únicos para cada transacción")
    public void testProcesarTransaccion_UniqueIds() {
        // When
        TransaccionModel transaccion1 = webpayApiService.procesarTransaccion(orderId, monto);
        TransaccionModel transaccion2 = webpayApiService.procesarTransaccion(orderId, monto);

        // Then
        assertNotNull(transaccion1.getId());
        assertNotNull(transaccion2.getId());
        assertNotEquals(transaccion1.getId(), transaccion2.getId());
    }

    @Test
    @DisplayName("Debería solicitar reembolso exitosamente")
    public void testSolicitarReembolso() {
        // When
        ReembolsoModel resultado = webpayApiService.solicitarReembolso(pagoId, monto);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(pagoId, resultado.getPagoId());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertNotNull(resultado.getFechaSolicitud());
        assertTrue(resultado.getFechaSolicitud().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(resultado.getFechaSolicitud().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debería solicitar reembolso con monto cero")
    public void testSolicitarReembolso_WithZeroAmount() {
        // Given
        BigDecimal montoCero = BigDecimal.ZERO;

        // When
        ReembolsoModel resultado = webpayApiService.solicitarReembolso(pagoId, montoCero);

        // Then
        assertNotNull(resultado);
        assertEquals(pagoId, resultado.getPagoId());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    @DisplayName("Debería generar IDs únicos para cada reembolso")
    public void testSolicitarReembolso_UniqueIds() {
        // When
        ReembolsoModel reembolso1 = webpayApiService.solicitarReembolso(pagoId, monto);
        ReembolsoModel reembolso2 = webpayApiService.solicitarReembolso(pagoId, monto);

        // Then
        assertNotNull(reembolso1.getId());
        assertNotNull(reembolso2.getId());
        assertNotEquals(reembolso1.getId(), reembolso2.getId());
    }

    @Test
    @DisplayName("Debería consultar estado de transacción exitosamente")
    public void testConsultarEstadoTransaccion() {
        // When
        TransaccionModel resultado = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Then
        assertNotNull(resultado);
        assertEquals(transaccionId, resultado.getId());
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals("WEBPAYPLUS", resultado.getProveedor());
        assertNull(resultado.getDetalleError());
        assertNotNull(resultado.getFechaTransaccion());
        assertTrue(resultado.getFechaTransaccion().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Debería consultar estado de transacción con ID nulo")
    public void testConsultarEstadoTransaccion_WithNullId() {
        // When
        TransaccionModel resultado = webpayApiService.consultarEstadoTransaccion(null);

        // Then
        assertNotNull(resultado);
        assertNull(resultado.getId());
        assertEquals("APROBADO", resultado.getEstado());
        assertEquals("WEBPAYPLUS", resultado.getProveedor());
    }

    @Test
    @DisplayName("Debería mantener consistencia en el proveedor")
    public void testProveedorConsistency() {
        // When
        TransaccionModel transaccion = webpayApiService.procesarTransaccion(orderId, monto);
        TransaccionModel consulta = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Then
        assertEquals("WEBPAYPLUS", transaccion.getProveedor());
        assertEquals("WEBPAYPLUS", consulta.getProveedor());
    }

    @Test
    @DisplayName("Debería mantener consistencia en el estado")
    public void testEstadoConsistency() {
        // When
        TransaccionModel transaccion = webpayApiService.procesarTransaccion(orderId, monto);
        TransaccionModel consulta = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Then
        assertEquals("APROBADO", transaccion.getEstado());
        assertEquals("APROBADO", consulta.getEstado());
    }

    @Test
    @DisplayName("Debería manejar múltiples solicitudes de reembolso")
    public void testMultipleReembolsos() {
        // When
        ReembolsoModel reembolso1 = webpayApiService.solicitarReembolso(pagoId, monto);
        ReembolsoModel reembolso2 = webpayApiService.solicitarReembolso(pagoId, monto);
        ReembolsoModel reembolso3 = webpayApiService.solicitarReembolso(pagoId, monto);

        // Then
        assertNotNull(reembolso1);
        assertNotNull(reembolso2);
        assertNotNull(reembolso3);
        assertNotEquals(reembolso1.getId(), reembolso2.getId());
        assertNotEquals(reembolso2.getId(), reembolso3.getId());
        assertNotEquals(reembolso1.getId(), reembolso3.getId());
    }
} 