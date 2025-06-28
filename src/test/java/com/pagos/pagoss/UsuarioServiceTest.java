package com.pagos.pagos;

import com.pagos.pagos.model.UsuarioModel;
import com.pagos.pagos.repository.UsuarioRepository;
import com.pagos.pagos.services.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testFindAll() {
        UsuarioModel user = new UsuarioModel();
        user.setId(UUID.randomUUID());
        user.setNombre("Juan");
        user.setEmail("juan@email.com");

        when(usuarioRepository.findAll()).thenReturn(List.of(user));

        List<UsuarioModel> usuarios = usuarioService.findAll();

        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("Juan", usuarios.get(0).getNombre());
    }

    @Test
    public void testFindById() {
        UUID id = UUID.randomUUID();
        UsuarioModel user = new UsuarioModel();
        user.setId(id);
        user.setNombre("María");
        user.setEmail("maria@email.com");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<UsuarioModel> result = usuarioService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("María", result.get().getNombre());
    }

    @Test
    public void testSave() {
        UsuarioModel user = new UsuarioModel();
        user.setId(UUID.randomUUID());
        user.setNombre("Luis");
        user.setEmail("luis@email.com");

        when(usuarioRepository.save(user)).thenReturn(user);

        UsuarioModel saved = usuarioService.save(user);

        assertNotNull(saved);
        assertEquals("Luis", saved.getNombre());
    }

    @Test
    public void testDeleteById() {
        UUID id = UUID.randomUUID();

        doNothing().when(usuarioRepository).deleteById(id);

        usuarioService.deleteById(id);

        verify(usuarioRepository, times(1)).deleteById(id);
    }
}
