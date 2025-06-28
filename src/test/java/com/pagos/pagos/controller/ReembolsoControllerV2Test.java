package com.pagos.pagos.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.services.ReembolsoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Controlador de Reembolsos V2")
public class ReembolsoControllerV2Test {

    @Mock
    private ReembolsoService reembolsoService;

    @InjectMocks
    private ReembolsoControllerV2 reembolsoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ReembolsoModel reembolsoExistente;
    private UUID pagoId;
    private UUID reembolsoId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reembolsoController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        pagoId = UUID.randomUUID();
        reembolsoId = UUID.randomUUID();
        
        reembolsoExistente = new ReembolsoModel();
        reembolsoExistente.setId(reembolsoId);
        reembolsoExistente.setPagoId(pagoId);
        reembolsoExistente.setMonto(new BigDecimal("15000.50"));
        reembolsoExistente.setFechaSolicitud(LocalDateTime.now());
        reembolsoExistente.setEstado("PROCESADO");
    }

    @Test
    @DisplayName("Crear reembolso exitosamente")
    void testCrearReembolso_Success() throws Exception {
        when(reembolsoService.procesarReembolso(any(UUID.class), any(BigDecimal.class)))
                .thenReturn(reembolsoExistente);

        mockMvc.perform(post("/api/v2/reembolsos")
                .param("pagoId", pagoId.toString())
                .param("monto", "15000.50")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(reembolsoId.toString()))
                .andExpect(jsonPath("$.pagoId").value(pagoId.toString()))
                .andExpect(jsonPath("$.monto").value(15000.50))
                .andExpect(jsonPath("$.estado").value("PROCESADO"));

        verify(reembolsoService).procesarReembolso(pagoId, new BigDecimal("15000.50"));
    }

    @Test
    @DisplayName("Obtener todos los reembolsos exitosamente")
    void testGetAllReembolsos_Success() throws Exception {
        List<ReembolsoModel> reembolsos = Arrays.asList(reembolsoExistente);
        when(reembolsoService.obtenerTodos()).thenReturn(reembolsos);

        mockMvc.perform(get("/api/v2/reembolsos")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$[0].id").value(reembolsoId.toString()))
                .andExpect(jsonPath("$[0].pagoId").value(pagoId.toString()));

        verify(reembolsoService).obtenerTodos();
    }

    @Test
    @DisplayName("Obtener reembolso por ID exitosamente")
    void testGetReembolsoById_Success() throws Exception {
        when(reembolsoService.obtenerPorId(reembolsoId)).thenReturn(reembolsoExistente);

        mockMvc.perform(get("/api/v2/reembolsos/{id}", reembolsoId)
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(reembolsoId.toString()))
                .andExpect(jsonPath("$.pagoId").value(pagoId.toString()));

        verify(reembolsoService).obtenerPorId(reembolsoId);
    }

    @Test
    @DisplayName("Eliminar reembolso por ID exitosamente")
    void testEliminarPorId_Success() throws Exception {
        doNothing().when(reembolsoService).eliminarPorId(reembolsoId);

        mockMvc.perform(delete("/api/v2/reembolsos/{id}", reembolsoId)
                .accept("application/hal+json"))
                .andExpect(status().isOk());

        verify(reembolsoService).eliminarPorId(reembolsoId);
    }

    @Test
    @DisplayName("Crear reembolso con monto inválido")
    void testCrearReembolso_InvalidMonto() throws Exception {
        mockMvc.perform(post("/api/v2/reembolsos")
                .param("pagoId", pagoId.toString())
                .param("monto", "-100")
                .accept("application/hal+json"))
                .andExpect(status().isOk()); // El controlador no valida el monto, solo lo pasa al servicio

        verify(reembolsoService).procesarReembolso(pagoId, new BigDecimal("-100"));
    }

    @Test
    @DisplayName("Obtener reembolso con ID inválido")
    void testGetReembolsoById_InvalidId() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/api/v2/reembolsos/{id}", invalidId)
                .accept("application/hal+json"))
                .andExpect(status().isBadRequest());

        verify(reembolsoService, never()).obtenerPorId(any());
    }

    @Test
    @DisplayName("Eliminar reembolso con ID inválido")
    void testEliminarPorId_InvalidId() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(delete("/api/v2/reembolsos/{id}", invalidId)
                .accept("application/hal+json"))
                .andExpect(status().isBadRequest());

        verify(reembolsoService, never()).eliminarPorId(any());
    }

    @Test
    @DisplayName("Crear reembolso con UUID inválido")
    void testCrearReembolso_InvalidPagoId() throws Exception {
        String invalidPagoId = "invalid-uuid";

        mockMvc.perform(post("/api/v2/reembolsos")
                .param("pagoId", invalidPagoId)
                .param("monto", "15000.50")
                .accept("application/hal+json"))
                .andExpect(status().isBadRequest());

        verify(reembolsoService, never()).procesarReembolso(any(), any());
    }

    @Test
    @DisplayName("Obtener lista vacía de reembolsos")
    void testGetAllReembolsos_EmptyList() throws Exception {
        when(reembolsoService.obtenerTodos()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v2/reembolsos")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(reembolsoService).obtenerTodos();
    }
} 