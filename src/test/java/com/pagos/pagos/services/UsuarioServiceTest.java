package com.pagos.pagos.services;

import com.pagos.pagos.model.UsuarioModel;
import com.pagos.pagos.repository.UsuarioRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas del Servicio de Usuarios")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private UsuarioModel usuario1;
    private UsuarioModel usuario2;
    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();

        usuario1 = new UsuarioModel();
        usuario1.setId(userId1);
        usuario1.setNombre("Juan Pérez");
        usuario1.setEmail("juan.perez@email.com");

        usuario2 = new UsuarioModel();
        usuario2.setId(userId2);
        usuario2.setNombre("María García");
        usuario2.setEmail("maria.garcia@email.com");
    }

    @Test
    @DisplayName("Debería encontrar todos los usuarios")
    public void testFindAll() {
        // Given
        List<UsuarioModel> usuarios = Arrays.asList(usuario1, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // When
        List<UsuarioModel> resultado = usuarioService.findAll();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(usuario1, resultado.get(0));
        assertEquals(usuario2, resultado.get(1));
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería encontrar un usuario por ID cuando existe")
    public void testFindById_WhenExists() {
        // Given
        when(usuarioRepository.findById(userId1)).thenReturn(Optional.of(usuario1));

        // When
        Optional<UsuarioModel> resultado = usuarioService.findById(userId1);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(usuario1, resultado.get());
        verify(usuarioRepository, times(1)).findById(userId1);
    }

    @Test
    @DisplayName("Debería retornar Optional vacío cuando el usuario no existe")
    public void testFindById_WhenNotExists() {
        // Given
        when(usuarioRepository.findById(userId1)).thenReturn(Optional.empty());

        // When
        Optional<UsuarioModel> resultado = usuarioService.findById(userId1);

        // Then
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findById(userId1);
    }

    @Test
    @DisplayName("Debería guardar un usuario exitosamente")
    public void testSave() {
        // Given
        UsuarioModel nuevoUsuario = new UsuarioModel();
        nuevoUsuario.setNombre("Luis Rodríguez");
        nuevoUsuario.setEmail("luis.rodriguez@email.com");

        UsuarioModel usuarioGuardado = new UsuarioModel();
        usuarioGuardado.setId(userId1);
        usuarioGuardado.setNombre("Luis Rodríguez");
        usuarioGuardado.setEmail("luis.rodriguez@email.com");

        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(usuarioGuardado);

        // When
        UsuarioModel resultado = usuarioService.save(nuevoUsuario);

        // Then
        assertNotNull(resultado);
        assertEquals(usuarioGuardado.getId(), resultado.getId());
        assertEquals(usuarioGuardado.getNombre(), resultado.getNombre());
        assertEquals(usuarioGuardado.getEmail(), resultado.getEmail());
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    @DisplayName("Debería eliminar un usuario por ID")
    public void testDeleteById() {
        // Given
        doNothing().when(usuarioRepository).deleteById(userId1);

        // When
        usuarioService.deleteById(userId1);

        // Then
        verify(usuarioRepository, times(1)).deleteById(userId1);
    }

    @Test
    @DisplayName("Debería manejar lista vacía de usuarios")
    public void testFindAll_WhenEmpty() {
        // Given
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<UsuarioModel> resultado = usuarioService.findAll();

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar excepción del repositorio")
    public void testFindAll_WhenRepositoryException() {
        // Given
        when(usuarioRepository.findAll()).thenThrow(new RuntimeException("Error de base de datos"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            usuarioService.findAll();
        });
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería manejar excepción al guardar")
    public void testSave_WhenRepositoryException() {
        // Given
        UsuarioModel nuevoUsuario = new UsuarioModel();
        nuevoUsuario.setNombre("Test");
        nuevoUsuario.setEmail("test@email.com");

        when(usuarioRepository.save(any(UsuarioModel.class)))
            .thenThrow(new RuntimeException("Error al guardar"));

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            usuarioService.save(nuevoUsuario);
        });
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    @DisplayName("Debería manejar excepción al eliminar")
    public void testDeleteById_WhenRepositoryException() {
        // Given
        doThrow(new RuntimeException("Error al eliminar")).when(usuarioRepository).deleteById(userId1);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            usuarioService.deleteById(userId1);
        });
        verify(usuarioRepository, times(1)).deleteById(userId1);
    }
}
