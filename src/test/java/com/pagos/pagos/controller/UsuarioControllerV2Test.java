package com.pagos.pagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagos.pagos.assemblers.UsuarioModelAssembler;
import com.pagos.pagos.model.UsuarioModel;
import com.pagos.pagos.services.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioControllerV2.class)
public class UsuarioControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioModel usuario;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        usuario = new UsuarioModel();
        usuario.setId(id);
        usuario.setNombre("Miguelito");
        usuario.setEmail("miguelito@email.com");
    }

    @Test
    public void testGetUsuarioById() throws Exception {
        when(usuarioService.findById(id)).thenReturn(Optional.of(usuario));
        when(assembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        mockMvc.perform(get("/api/v2/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Miguelito"))
                .andExpect(jsonPath("$.email").value("miguelito@email.com"));
    }

    @Test
    public void testCreateUsuario() throws Exception {
        when(usuarioService.save(any(UsuarioModel.class))).thenReturn(usuario);
        when(assembler.toModel(any(UsuarioModel.class))).thenReturn(EntityModel.of(usuario));

        mockMvc.perform(post("/api/v2/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Miguelito"))
                .andExpect(jsonPath("$.email").value("miguelito@email.com"));
    }

    @Test
    public void testGetAllUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of(usuario));
        when(assembler.toModel(usuario)).thenReturn(EntityModel.of(usuario));

        mockMvc.perform(get("/api/v2/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioModelList[0].nombre").value("Miguelito"))
                .andExpect(jsonPath("$._embedded.usuarioModelList[0].email").value("miguelito@email.com"));
    }

    @Test
    public void testDeleteUsuario() throws Exception {
        doNothing().when(usuarioService).deleteById(id);

        mockMvc.perform(delete("/api/v2/usuarios/{id}", id))
                .andExpect(status().isNoContent());
    }
}
