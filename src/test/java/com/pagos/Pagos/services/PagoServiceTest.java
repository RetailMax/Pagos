package com.pagos.pagos.services;

import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.repository.PagoRepository;
import com.pagos.pagos.repository.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private WebpayApiService webpayApiService;

    @InjectMocks
    private PagoService pagoService;

    private PagoModel pago;
    private TransaccionModel transaccion;
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
        pago.setMonto(BigDecimal.valueOf(5000));
        pago.setEstado("Aprobado");
        pago.setFechaPago(LocalDateTime.now());
        pago.setOrderId(orderId);
        pago.setUsuarioId(usuarioId);
        pago.setTransaccionId(transaccionId);

        transaccion = new TransaccionModel();
        transaccion.setId(transaccionId);
        transaccion.setEstado("APROBADO");
        transaccion.setProveedor("WEBPAYPLUS");
        transaccion.setMonto(BigDecimal.valueOf(5000));
        transaccion.setFechaTransaccion(LocalDateTime.now());
    }

    @Test
    void testFindAll() {
        // Arrange
        List<PagoModel> expectedPagos = Arrays.asList(pago);
        when(pagoRepository.findAll()).thenReturn(expectedPagos);

        // Act
        List<PagoModel> result = pagoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pago, result.get(0));
        verify(pagoRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<PagoModel> result = pagoService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pagoRepository).findAll();
    }

    @Test
    void testFindAll_MultiplePagos() {
        // Arrange
        PagoModel pago2 = new PagoModel();
        pago2.setId(UUID.randomUUID());
        pago2.setMonto(BigDecimal.valueOf(3000));
        pago2.setEstado("Rechazado");

        List<PagoModel> expectedPagos = Arrays.asList(pago, pago2);
        when(pagoRepository.findAll()).thenReturn(expectedPagos);

        // Act
        List<PagoModel> result = pagoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(pago, result.get(0));
        assertEquals(pago2, result.get(1));
        verify(pagoRepository).findAll();
    }

    @Test
    void testSave() {
        // Arrange
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.save(pago);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result);
        verify(pagoRepository).save(pago);
    }

    @Test
    void testSave_WithNewPago() {
        // Arrange
        PagoModel newPago = new PagoModel();
        newPago.setMonto(BigDecimal.valueOf(10000));
        newPago.setEstado("Pendiente");
        newPago.setFechaPago(LocalDateTime.now());
        newPago.setOrderId(UUID.randomUUID());
        newPago.setUsuarioId(UUID.randomUUID());
        newPago.setTransaccionId(UUID.randomUUID());

        PagoModel savedPago = new PagoModel();
        savedPago.setId(UUID.randomUUID());
        savedPago.setMonto(newPago.getMonto());
        savedPago.setEstado(newPago.getEstado());
        savedPago.setFechaPago(newPago.getFechaPago());
        savedPago.setOrderId(newPago.getOrderId());
        savedPago.setUsuarioId(newPago.getUsuarioId());
        savedPago.setTransaccionId(newPago.getTransaccionId());

        when(pagoRepository.save(any(PagoModel.class))).thenReturn(savedPago);

        // Act
        PagoModel result = pagoService.save(newPago);

        // Assert
        assertNotNull(result);
        assertEquals(savedPago, result);
        verify(pagoRepository).save(newPago);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(pagoRepository).deleteById(pagoId);

        // Act
        pagoService.deleteById(pagoId);

        // Assert
        verify(pagoRepository).deleteById(pagoId);
    }

    @Test
    void testDeleteById_NonExistentId() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        doNothing().when(pagoRepository).deleteById(nonExistentId);

        // Act
        pagoService.deleteById(nonExistentId);

        // Assert
        verify(pagoRepository).deleteById(nonExistentId);
    }

    @Test
    void testObtenerPagoPorId() {
        // Arrange
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        // Act
        PagoModel result = pagoService.obtenerPagoPorId(pagoId);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result);
        verify(pagoRepository).findById(pagoId);
    }

    @Test
    void testObtenerPagoPorId_NotFound() {
        // Arrange
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        // Act
        PagoModel result = pagoService.obtenerPagoPorId(pagoId);

        // Assert
        assertNull(result);
        verify(pagoRepository).findById(pagoId);
    }

    @Test
    void testObtenerPagoPorId_WithDifferentId() {
        // Arrange
        UUID differentId = UUID.randomUUID();
        when(pagoRepository.findById(differentId)).thenReturn(Optional.of(pago));

        // Act
        PagoModel result = pagoService.obtenerPagoPorId(differentId);

        // Assert
        assertNotNull(result);
        assertEquals(pago, result);
        verify(pagoRepository).findById(differentId);
    }

    @Test
    void testActualizarEstadoPago() {
        // Arrange
        String nuevoEstado = "Rechazado";
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Assert
        verify(pagoRepository).findById(pagoId);
        verify(pagoRepository).save(pago);
        assertEquals(nuevoEstado, pago.getEstado());
    }

    @Test
    void testActualizarEstadoPago_NotFound() {
        // Arrange
        String nuevoEstado = "Rechazado";
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        // Act
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Assert
        verify(pagoRepository).findById(pagoId);
        verify(pagoRepository, never()).save(any(PagoModel.class));
    }

    @Test
    void testActualizarEstadoPago_WithEmptyString() {
        // Arrange
        String nuevoEstado = "";
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Assert
        verify(pagoRepository).findById(pagoId);
        verify(pagoRepository).save(pago);
        assertEquals(nuevoEstado, pago.getEstado());
    }

    @Test
    void testActualizarEstadoPago_WithNullString() {
        // Arrange
        String nuevoEstado = null;
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Assert
        verify(pagoRepository).findById(pagoId);
        verify(pagoRepository).save(pago);
        assertEquals(nuevoEstado, pago.getEstado());
    }

    @Test
    void testProcesarPago() {
        // Arrange
        BigDecimal monto = BigDecimal.valueOf(5000);
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        verify(webpayApiService).procesarTransaccion(orderId, monto);
        verify(transaccionRepository).save(transaccion);
        verify(pagoRepository).save(any(PagoModel.class));
    }

    @Test
    void testProcesarPago_WithZeroAmount() {
        // Arrange
        BigDecimal monto = BigDecimal.ZERO;
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        verify(webpayApiService).procesarTransaccion(orderId, monto);
        verify(transaccionRepository).save(transaccion);
        verify(pagoRepository).save(any(PagoModel.class));
    }

    @Test
    void testProcesarPago_WithNegativeAmount() {
        // Arrange
        BigDecimal monto = BigDecimal.valueOf(-1000);
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        verify(webpayApiService).procesarTransaccion(orderId, monto);
        verify(transaccionRepository).save(transaccion);
        verify(pagoRepository).save(any(PagoModel.class));
    }

    @Test
    void testProcesarPago_WithLargeAmount() {
        // Arrange
        BigDecimal monto = BigDecimal.valueOf(999999.99);
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        verify(webpayApiService).procesarTransaccion(orderId, monto);
        verify(transaccionRepository).save(transaccion);
        verify(pagoRepository).save(any(PagoModel.class));
    }

    @Test
    void testProcesarPago_WithRejectedTransaction() {
        // Arrange
        BigDecimal monto = BigDecimal.valueOf(5000);
        TransaccionModel rejectedTransaccion = new TransaccionModel();
        rejectedTransaccion.setId(transaccionId);
        rejectedTransaccion.setEstado("RECHAZADO");
        rejectedTransaccion.setProveedor("WEBPAYPLUS");
        rejectedTransaccion.setMonto(monto);
        rejectedTransaccion.setFechaTransaccion(LocalDateTime.now());
        rejectedTransaccion.setDetalleError("Saldo insuficiente");

        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(rejectedTransaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(rejectedTransaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        verify(webpayApiService).procesarTransaccion(orderId, monto);
        verify(transaccionRepository).save(rejectedTransaccion);
        verify(pagoRepository).save(any(PagoModel.class));
    }

    @Test
    void testProcesarPago_VerifyPagoCreation() {
        // Arrange
        BigDecimal monto = BigDecimal.valueOf(5000);
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenAnswer(invocation -> {
            PagoModel pagoToSave = invocation.getArgument(0);
            pagoToSave.setId(UUID.randomUUID());
            return pagoToSave;
        });

        // Act
        PagoModel result = pagoService.procesarPago(orderId, usuarioId, monto);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(usuarioId, result.getUsuarioId());
        assertEquals(monto, result.getMonto());
        assertEquals(transaccion.getEstado(), result.getEstado());
        assertEquals(transaccion.getId(), result.getTransaccionId());
        assertNotNull(result.getFechaPago());
        verify(pagoRepository).save(argThat(pago -> 
            orderId.equals(pago.getOrderId()) &&
            usuarioId.equals(pago.getUsuarioId()) &&
            monto.equals(pago.getMonto()) &&
            transaccion.getEstado().equals(pago.getEstado()) &&
            transaccion.getId().equals(pago.getTransaccionId())
        ));
    }
}
