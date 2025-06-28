package com.pagos.pagos.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.repository.PagoRepository;
import com.pagos.pagos.repository.TransaccionRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas del Servicio de Pagos")
public class PagoServiceTest {

    @Autowired
    private PagoService pagoService;

    @MockBean
    private PagoRepository pagoRepository;

    @MockBean
    private TransaccionRepository transaccionRepository;

    @MockBean
    private WebpayApiService webpayApiService;

    private UUID orderId;
    private UUID usuarioId;
    private UUID transaccionId;
    private UUID pagoId;
    private BigDecimal monto;
    private TransaccionModel transaccion;
    private PagoModel pago;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        transaccionId = UUID.randomUUID();
        pagoId = UUID.randomUUID();
        monto = new BigDecimal("50000.00");

        transaccion = new TransaccionModel();
        transaccion.setId(transaccionId);
        transaccion.setPagoId(pagoId);
        transaccion.setMonto(monto);
        transaccion.setEstado("Aprobado");
        transaccion.setProveedor("WebpayPlus");
        transaccion.setFechaTransaccion(LocalDateTime.now());

        pago = new PagoModel();
        pago.setId(pagoId);
        pago.setOrderId(orderId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(monto);
        pago.setEstado("Aprobado");
        pago.setFechaPago(LocalDateTime.now());
        pago.setTransaccionId(transaccionId);
    }

    @Test
    @DisplayName("Debería procesar un pago exitosamente")
    public void testProcesarPago_Success() {
        // Given
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // When
        PagoModel resultado = pagoService.procesarPago(orderId, usuarioId, monto);

        // Then
        assertNotNull(resultado);
        assertEquals(orderId, resultado.getOrderId());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(monto, resultado.getMonto());
        assertEquals("Aprobado", resultado.getEstado());
        assertEquals(transaccionId, resultado.getTransaccionId());
        
        verify(webpayApiService, times(1)).procesarTransaccion(orderId, monto);
        verify(transaccionRepository, times(1)).save(any(TransaccionModel.class));
        verify(pagoRepository, times(1)).save(any(PagoModel.class));
    }

    @Test
    @DisplayName("Debería procesar un pago rechazado")
    public void testProcesarPago_Rejected() {
        // Given
        transaccion.setEstado("Rechazado");
        pago.setEstado("Rechazado");
        
        when(webpayApiService.procesarTransaccion(orderId, monto)).thenReturn(transaccion);
        when(transaccionRepository.save(any(TransaccionModel.class))).thenReturn(transaccion);
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // When
        PagoModel resultado = pagoService.procesarPago(orderId, usuarioId, monto);

        // Then
        assertNotNull(resultado);
        assertEquals("Rechazado", resultado.getEstado());
        
        verify(webpayApiService, times(1)).procesarTransaccion(orderId, monto);
        verify(transaccionRepository, times(1)).save(any(TransaccionModel.class));
        verify(pagoRepository, times(1)).save(any(PagoModel.class));
    }

    @Test
    @DisplayName("Debería encontrar todos los pagos")
    public void testFindAll() {
        // Given
        List<PagoModel> pagos = Arrays.asList(pago);
        when(pagoRepository.findAll()).thenReturn(pagos);

        // When
        List<PagoModel> resultado = pagoService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(pago, resultado.get(0));
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería guardar un pago")
    public void testSave() {
        // Given
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // When
        PagoModel resultado = pagoService.save(pago);

        // Then
        assertNotNull(resultado);
        assertEquals(pagoId, resultado.getId());
        assertEquals(orderId, resultado.getOrderId());
        verify(pagoRepository, times(1)).save(pago);
    }

    @Test
    @DisplayName("Debería eliminar un pago por ID")
    public void testDeleteById() {
        // Given
        doNothing().when(pagoRepository).deleteById(pagoId);

        // When
        pagoService.deleteById(pagoId);

        // Then
        verify(pagoRepository, times(1)).deleteById(pagoId);
    }

    @Test
    @DisplayName("Debería obtener un pago por ID cuando existe")
    public void testObtenerPagoPorId_WhenExists() {
        // Given
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));

        // When
        PagoModel resultado = pagoService.obtenerPagoPorId(pagoId);

        // Then
        assertNotNull(resultado);
        assertEquals(pagoId, resultado.getId());
        assertEquals(orderId, resultado.getOrderId());
        verify(pagoRepository, times(1)).findById(pagoId);
    }

    @Test
    @DisplayName("Debería retornar null cuando el pago no existe")
    public void testObtenerPagoPorId_WhenNotExists() {
        // Given
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        // When
        PagoModel resultado = pagoService.obtenerPagoPorId(pagoId);

        // Then
        assertNull(resultado);
        verify(pagoRepository, times(1)).findById(pagoId);
    }

    @Test
    @DisplayName("Debería actualizar el estado de un pago existente")
    public void testActualizarEstadoPago_WhenPagoExists() {
        // Given
        String nuevoEstado = "Completado";
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(PagoModel.class))).thenReturn(pago);

        // When
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Then
        verify(pagoRepository, times(1)).findById(pagoId);
        verify(pagoRepository, times(1)).save(any(PagoModel.class));
    }

    @Test
    @DisplayName("Debería manejar actualización de estado cuando el pago no existe")
    public void testActualizarEstadoPago_WhenPagoNotExists() {
        // Given
        String nuevoEstado = "Completado";
        when(pagoRepository.findById(pagoId)).thenReturn(Optional.empty());

        // When
        pagoService.actualizarEstadoPago(pagoId, nuevoEstado);

        // Then
        verify(pagoRepository, times(1)).findById(pagoId);
        verify(pagoRepository, never()).save(any(PagoModel.class));
    }

    @Test
    @DisplayName("Debería manejar lista vacía de pagos")
    public void testFindAll_WhenNoPagos() {
        // Given
        when(pagoRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<PagoModel> resultado = pagoService.findAll();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar excepción al procesar pago")
    public void testProcesarPago_WhenExceptionOccurs() {
        // Given
        when(webpayApiService.procesarTransaccion(orderId, monto))
            .thenThrow(new RuntimeException("Error en Webpay"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            pagoService.procesarPago(orderId, usuarioId, monto);
        });
        
        verify(webpayApiService, times(1)).procesarTransaccion(orderId, monto);
        verify(transaccionRepository, never()).save(any(TransaccionModel.class));
        verify(pagoRepository, never()).save(any(PagoModel.class));
    }
}
