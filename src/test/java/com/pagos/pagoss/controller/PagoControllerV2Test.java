package com.pagos.pagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagos.pagos.assemblers.PagoModelAssembler;
import com.pagos.pagos.model.PagoModel;
import com.pagos.pagos.services.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PagoControllerV2Test {

    @Mock
    private PagoService pagoService;

    @Mock
    private PagoModelAssembler assembler;

    @InjectMocks
    private PagoControllerV2 pagoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PagoModel pagoExistente;
    private PagoModel pagoNuevo;
    private UUID pagoId;
    private EntityModel<PagoModel> entityModel;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pagoController).build();
        objectMapper = new ObjectMapper();
        
        pagoId = UUID.randomUUID();
        pagoExistente = new PagoModel();
        pagoExistente.setId(pagoId);
        pagoExistente.setMonto(BigDecimal.valueOf(5000));
        pagoExistente.setEstado("Aprobado");
        pagoExistente.setFechaPago(LocalDateTime.now());
        pagoExistente.setOrderId(UUID.randomUUID());
        pagoExistente.setUsuarioId(UUID.randomUUID());
        pagoExistente.setTransaccionId(UUID.randomUUID());

        pagoNuevo = new PagoModel();
        pagoNuevo.setMonto(BigDecimal.valueOf(9990));
        pagoNuevo.setEstado("Pendiente");
        pagoNuevo.setFechaPago(LocalDateTime.now());
        pagoNuevo.setOrderId(UUID.randomUUID());
        pagoNuevo.setUsuarioId(UUID.randomUUID());
        pagoNuevo.setTransaccionId(UUID.randomUUID());

        entityModel = EntityModel.of(pagoExistente);
    }

    @Test
    void testGetAllPagos_Success() throws Exception {
        // Arrange
        List<PagoModel> pagos = Arrays.asList(pagoExistente);
        when(pagoService.findAll()).thenReturn(pagos);
        when(assembler.toModel(any(PagoModel.class))).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(pagoService).findAll();
        verify(assembler, times(1)).toModel(any(PagoModel.class));
    }

    @Test
    void testGetAllPagos_EmptyList() throws Exception {
        // Arrange
        when(pagoService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(pagoService).findAll();
        verify(assembler, never()).toModel(any(PagoModel.class));
    }

    @Test
    void testGetAllPagos_MultiplePagos() throws Exception {
        // Arrange
        PagoModel pago2 = new PagoModel();
        pago2.setId(UUID.randomUUID());
        pago2.setMonto(BigDecimal.valueOf(3000));
        pago2.setEstado("Rechazado");
        
        List<PagoModel> pagos = Arrays.asList(pagoExistente, pago2);
        when(pagoService.findAll()).thenReturn(pagos);
        when(assembler.toModel(any(PagoModel.class))).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(pagoService).findAll();
        verify(assembler, times(2)).toModel(any(PagoModel.class));
    }

    @Test
    void testGetPagoById_Success() throws Exception {
        // Arrange
        when(pagoService.obtenerPagoPorId(pagoId)).thenReturn(pagoExistente);
        when(assembler.toModel(pagoExistente)).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos/" + pagoId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(pagoService).obtenerPagoPorId(pagoId);
        verify(assembler).toModel(pagoExistente);
    }

    @Test
    void testGetPagoById_NotFound() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(pagoService.obtenerPagoPorId(nonExistentId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos/" + nonExistentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(pagoService).obtenerPagoPorId(nonExistentId);
        verify(assembler, never()).toModel(any(PagoModel.class));
    }

    @Test
    void testCreatePago_Success() throws Exception {
        // Arrange
        PagoModel savedPago = new PagoModel();
        savedPago.setId(UUID.randomUUID());
        savedPago.setMonto(pagoNuevo.getMonto());
        savedPago.setEstado(pagoNuevo.getEstado());
        savedPago.setFechaPago(pagoNuevo.getFechaPago());
        savedPago.setOrderId(pagoNuevo.getOrderId());
        savedPago.setUsuarioId(pagoNuevo.getUsuarioId());
        savedPago.setTransaccionId(pagoNuevo.getTransaccionId());

        EntityModel<PagoModel> savedEntityModel = EntityModel.of(savedPago);
        
        when(pagoService.save(any(PagoModel.class))).thenReturn(savedPago);
        when(assembler.toModel(savedPago)).thenReturn(savedEntityModel);

        // Act & Assert
        mockMvc.perform(post("/api/v2/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoNuevo)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(pagoService).save(any(PagoModel.class));
        verify(assembler).toModel(savedPago);
    }

    @Test
    void testCreatePago_WithInvalidData() throws Exception {
        // Arrange
        PagoModel invalidPago = new PagoModel();
        // Sin datos requeridos

        // Act & Assert
        mockMvc.perform(post("/api/v2/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPago)))
                .andExpect(status().isCreated()); // El controlador no valida, solo guarda

        verify(pagoService).save(any(PagoModel.class));
    }

    @Test
    void testCreatePago_WithNullData() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v2/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePago_Success() throws Exception {
        // Arrange
        pagoExistente.setMonto(BigDecimal.valueOf(12345));
        
        when(pagoService.save(any(PagoModel.class))).thenReturn(pagoExistente);
        when(assembler.toModel(pagoExistente)).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put("/api/v2/pagos/" + pagoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoExistente)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(pagoService).save(any(PagoModel.class));
        verify(assembler).toModel(pagoExistente);
    }

    @Test
    void testUpdatePago_WithDifferentId() throws Exception {
        // Arrange
        UUID differentId = UUID.randomUUID();
        pagoExistente.setMonto(BigDecimal.valueOf(12345));
        
        when(pagoService.save(any(PagoModel.class))).thenReturn(pagoExistente);
        when(assembler.toModel(pagoExistente)).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put("/api/v2/pagos/" + differentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoExistente)))
                .andExpect(status().isOk());

        verify(pagoService).save(any(PagoModel.class));
        // Verificar que el ID se establece correctamente
        verify(pagoService).save(argThat(pago -> differentId.equals(pago.getId())));
    }

    @Test
    void testUpdatePago_WithInvalidData() throws Exception {
        // Arrange
        PagoModel invalidPago = new PagoModel();
        invalidPago.setMonto(BigDecimal.valueOf(-1000)); // Monto negativo

        when(pagoService.save(any(PagoModel.class))).thenReturn(invalidPago);
        when(assembler.toModel(invalidPago)).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(put("/api/v2/pagos/" + pagoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPago)))
                .andExpect(status().isOk());

        verify(pagoService).save(any(PagoModel.class));
    }

    @Test
    void testDeletePago_Success() throws Exception {
        // Arrange
        doNothing().when(pagoService).deleteById(pagoId);

        // Act & Assert
        mockMvc.perform(delete("/api/v2/pagos/" + pagoId))
                .andExpect(status().isNoContent());

        verify(pagoService).deleteById(pagoId);
    }

    @Test
    void testDeletePago_NonExistentId() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        doNothing().when(pagoService).deleteById(nonExistentId);

        // Act & Assert
        mockMvc.perform(delete("/api/v2/pagos/" + nonExistentId))
                .andExpect(status().isNoContent());

        verify(pagoService).deleteById(nonExistentId);
    }

    @Test
    void testGetPagoById_WithInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos/invalid-uuid")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePago_WithInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v2/pagos/invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoExistente)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeletePago_WithInvalidUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v2/pagos/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllPagos_WithDifferentAcceptHeader() throws Exception {
        // Arrange
        List<PagoModel> pagos = Arrays.asList(pagoExistente);
        when(pagoService.findAll()).thenReturn(pagos);
        when(assembler.toModel(any(PagoModel.class))).thenReturn(entityModel);

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());

        verify(pagoService).findAll();
    }

    @Test
    void testCreatePago_WithDifferentContentType() throws Exception {
        // Arrange
        PagoModel savedPago = new PagoModel();
        savedPago.setId(UUID.randomUUID());
        savedPago.setMonto(pagoNuevo.getMonto());
        savedPago.setEstado(pagoNuevo.getEstado());
        
        EntityModel<PagoModel> savedEntityModel = EntityModel.of(savedPago);
        
        when(pagoService.save(any(PagoModel.class))).thenReturn(savedPago);
        when(assembler.toModel(savedPago)).thenReturn(savedEntityModel);

        // Act & Assert
        mockMvc.perform(post("/api/v2/pagos")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(objectMapper.writeValueAsString(pagoNuevo)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void testGetPagoById_ServiceThrowsException() throws Exception {
        // Arrange
        when(pagoService.obtenerPagoPorId(pagoId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(get("/api/v2/pagos/" + pagoId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(pagoService).obtenerPagoPorId(pagoId);
    }

    @Test
    void testCreatePago_ServiceThrowsException() throws Exception {
        // Arrange
        when(pagoService.save(any(PagoModel.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(post("/api/v2/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoNuevo)))
                .andExpect(status().isInternalServerError());

        verify(pagoService).save(any(PagoModel.class));
    }

    @Test
    void testUpdatePago_ServiceThrowsException() throws Exception {
        // Arrange
        when(pagoService.save(any(PagoModel.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        mockMvc.perform(put("/api/v2/pagos/" + pagoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoExistente)))
                .andExpect(status().isInternalServerError());

        verify(pagoService).save(any(PagoModel.class));
    }

    @Test
    void testDeletePago_ServiceThrowsException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(pagoService).deleteById(pagoId);

        // Act & Assert
        mockMvc.perform(delete("/api/v2/pagos/" + pagoId))
                .andExpect(status().isInternalServerError());

        verify(pagoService).deleteById(pagoId);
    }
}
