package com.pagos.pagos.services;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.model.TransaccionModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WebpayApiServiceTest {

    @InjectMocks
    private WebpayApiService webpayApiService;

    private UUID orderId;
    private UUID pagoId;
    private BigDecimal monto;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        pagoId = UUID.randomUUID();
        monto = BigDecimal.valueOf(5000);
    }

    @Test
    void testProcesarTransaccion() {
        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("APROBADO", result.getEstado());
        assertEquals("WEBPAYPLUS", result.getProveedor());
        assertEquals(monto, result.getMonto());
        assertNotNull(result.getFechaTransaccion());
        assertNull(result.getDetalleError());
    }

    @Test
    void testProcesarTransaccion_WithZeroAmount() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, zeroAmount);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertEquals(zeroAmount, result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_WithNegativeAmount() {
        // Arrange
        BigDecimal negativeAmount = BigDecimal.valueOf(-1000);

        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, negativeAmount);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertEquals(negativeAmount, result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_WithLargeAmount() {
        // Arrange
        BigDecimal largeAmount = BigDecimal.valueOf(999999.99);

        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, largeAmount);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertEquals(largeAmount, result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_WithDecimalAmount() {
        // Arrange
        BigDecimal decimalAmount = BigDecimal.valueOf(1234.56);

        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, decimalAmount);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertEquals(decimalAmount, result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_WithNullOrderId() {
        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(null, monto);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertEquals(monto, result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_WithNullAmount() {
        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, null);

        // Assert
        assertNotNull(result);
        assertEquals("APROBADO", result.getEstado());
        assertNull(result.getMonto());
        assertNotNull(result.getId());
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_MultipleCalls() {
        // Act
        TransaccionModel result1 = webpayApiService.procesarTransaccion(orderId, monto);
        TransaccionModel result2 = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("APROBADO", result1.getEstado());
        assertEquals("APROBADO", result2.getEstado());
        assertEquals(monto, result1.getMonto());
        assertEquals(monto, result2.getMonto());
        
        // Los IDs deberían ser diferentes
        assertNotEquals(result1.getId(), result2.getId());
    }

    @Test
    void testProcesarTransaccion_VerifyTimestamp() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getFechaTransaccion());
        assertTrue(result.getFechaTransaccion().isAfter(beforeCall) || 
                  result.getFechaTransaccion().isEqual(beforeCall));
    }

    @Test
    void testSolicitarReembolso() {
        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(pagoId, monto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(pagoId, result.getPagoId());
        assertEquals("PENDIENTE", result.getEstado());
        assertNotNull(result.getFechaSolicitud());
    }

    @Test
    void testSolicitarReembolso_WithZeroAmount() {
        // Arrange
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(pagoId, zeroAmount);

        // Assert
        assertNotNull(result);
        assertEquals(pagoId, result.getPagoId());
        assertEquals("PENDIENTE", result.getEstado());
        assertNotNull(result.getId());
        assertNotNull(result.getFechaSolicitud());
    }

    @Test
    void testSolicitarReembolso_WithNegativeAmount() {
        // Arrange
        BigDecimal negativeAmount = BigDecimal.valueOf(-1000);

        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(pagoId, negativeAmount);

        // Assert
        assertNotNull(result);
        assertEquals(pagoId, result.getPagoId());
        assertEquals("PENDIENTE", result.getEstado());
        assertNotNull(result.getId());
        assertNotNull(result.getFechaSolicitud());
    }

    @Test
    void testSolicitarReembolso_WithNullPagoId() {
        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(null, monto);

        // Assert
        assertNotNull(result);
        assertNull(result.getPagoId());
        assertEquals("PENDIENTE", result.getEstado());
        assertNotNull(result.getId());
        assertNotNull(result.getFechaSolicitud());
    }

    @Test
    void testSolicitarReembolso_WithNullAmount() {
        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(pagoId, null);

        // Assert
        assertNotNull(result);
        assertEquals(pagoId, result.getPagoId());
        assertEquals("PENDIENTE", result.getEstado());
        assertNotNull(result.getId());
        assertNotNull(result.getFechaSolicitud());
    }

    @Test
    void testSolicitarReembolso_MultipleCalls() {
        // Act
        ReembolsoModel result1 = webpayApiService.solicitarReembolso(pagoId, monto);
        ReembolsoModel result2 = webpayApiService.solicitarReembolso(pagoId, monto);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(pagoId, result1.getPagoId());
        assertEquals(pagoId, result2.getPagoId());
        assertEquals("PENDIENTE", result1.getEstado());
        assertEquals("PENDIENTE", result2.getEstado());
        
        // Los IDs deberían ser diferentes
        assertNotEquals(result1.getId(), result2.getId());
    }

    @Test
    void testSolicitarReembolso_VerifyTimestamp() {
        // Arrange
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        ReembolsoModel result = webpayApiService.solicitarReembolso(pagoId, monto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getFechaSolicitud());
        assertTrue(result.getFechaSolicitud().isAfter(beforeCall) || 
                  result.getFechaSolicitud().isEqual(beforeCall));
    }

    @Test
    void testConsultarEstadoTransaccion() {
        // Arrange
        UUID transaccionId = UUID.randomUUID();

        // Act
        TransaccionModel result = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Assert
        assertNotNull(result);
        assertEquals(transaccionId, result.getId());
        assertEquals("APROBADO", result.getEstado());
        assertEquals("WEBPAYPLUS", result.getProveedor());
        assertNotNull(result.getFechaTransaccion());
        assertNull(result.getDetalleError());
    }

    @Test
    void testConsultarEstadoTransaccion_WithNullId() {
        // Act
        TransaccionModel result = webpayApiService.consultarEstadoTransaccion(null);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("APROBADO", result.getEstado());
        assertEquals("WEBPAYPLUS", result.getProveedor());
        assertNotNull(result.getFechaTransaccion());
    }

    @Test
    void testConsultarEstadoTransaccion_MultipleCalls() {
        // Arrange
        UUID transaccionId1 = UUID.randomUUID();
        UUID transaccionId2 = UUID.randomUUID();

        // Act
        TransaccionModel result1 = webpayApiService.consultarEstadoTransaccion(transaccionId1);
        TransaccionModel result2 = webpayApiService.consultarEstadoTransaccion(transaccionId2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(transaccionId1, result1.getId());
        assertEquals(transaccionId2, result2.getId());
        assertEquals("APROBADO", result1.getEstado());
        assertEquals("APROBADO", result2.getEstado());
    }

    @Test
    void testConsultarEstadoTransaccion_VerifyPastTimestamp() {
        // Arrange
        UUID transaccionId = UUID.randomUUID();
        LocalDateTime beforeCall = LocalDateTime.now();

        // Act
        TransaccionModel result = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getFechaTransaccion());
        // La fecha debería ser en el pasado (5 minutos antes)
        assertTrue(result.getFechaTransaccion().isBefore(beforeCall));
    }

    @Test
    void testProcesarTransaccion_VerifyUUIDGeneration() {
        // Act
        TransaccionModel result1 = webpayApiService.procesarTransaccion(orderId, monto);
        TransaccionModel result2 = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertNotEquals(result1.getId(), result2.getId());
        
        // Verificar que son UUIDs válidos
        assertDoesNotThrow(() -> UUID.fromString(result1.getId().toString()));
        assertDoesNotThrow(() -> UUID.fromString(result2.getId().toString()));
    }

    @Test
    void testSolicitarReembolso_VerifyUUIDGeneration() {
        // Act
        ReembolsoModel result1 = webpayApiService.solicitarReembolso(pagoId, monto);
        ReembolsoModel result2 = webpayApiService.solicitarReembolso(pagoId, monto);

        // Assert
        assertNotNull(result1.getId());
        assertNotNull(result2.getId());
        assertNotEquals(result1.getId(), result2.getId());
        
        // Verificar que son UUIDs válidos
        assertDoesNotThrow(() -> UUID.fromString(result1.getId().toString()));
        assertDoesNotThrow(() -> UUID.fromString(result2.getId().toString()));
    }

    @Test
    void testProcesarTransaccion_VerifyProveedor() {
        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result);
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testConsultarEstadoTransaccion_VerifyProveedor() {
        // Arrange
        UUID transaccionId = UUID.randomUUID();

        // Act
        TransaccionModel result = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Assert
        assertNotNull(result);
        assertEquals("WEBPAYPLUS", result.getProveedor());
    }

    @Test
    void testProcesarTransaccion_VerifyDetalleError() {
        // Act
        TransaccionModel result = webpayApiService.procesarTransaccion(orderId, monto);

        // Assert
        assertNotNull(result);
        assertNull(result.getDetalleError());
    }

    @Test
    void testConsultarEstadoTransaccion_VerifyDetalleError() {
        // Arrange
        UUID transaccionId = UUID.randomUUID();

        // Act
        TransaccionModel result = webpayApiService.consultarEstadoTransaccion(transaccionId);

        // Assert
        assertNotNull(result);
        assertNull(result.getDetalleError());
    }
} 