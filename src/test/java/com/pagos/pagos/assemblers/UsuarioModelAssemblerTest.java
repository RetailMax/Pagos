package com.pagos.pagos.assemblers;

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

import com.pagos.pagos.model.UsuarioModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Ensamblador de Modelo de Usuario")
public class UsuarioModelAssemblerTest {

    @InjectMocks
    private UsuarioModelAssembler assembler;

    private UsuarioModel usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new UsuarioModel();
        usuario.setId(usuarioId);
        usuario.setNombre("Juan Perez");
        usuario.setEmail("juan@correo.com");
    }

    @Test
    @DisplayName("Debería crear EntityModel correctamente")
    public void testToModel() {
        EntityModel<UsuarioModel> entityModel = assembler.toModel(usuario);
        assertNotNull(entityModel);
        assertEquals(usuario, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
        assertFalse(entityModel.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Debería contener enlace self")
    public void testToModel_ContainsSelfLink() {
        EntityModel<UsuarioModel> entityModel = assembler.toModel(usuario);
        assertTrue(entityModel.hasLink("self"));
        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains(usuarioId.toString()));
    }

    @Test
    @DisplayName("Debería contener enlace a usuarios")
    public void testToModel_ContainsUsuariosLink() {
        EntityModel<UsuarioModel> entityModel = assembler.toModel(usuario);
        assertTrue(entityModel.hasLink("usuarios"));
        Link usuariosLink = entityModel.getLink("usuarios").orElse(null);
        assertNotNull(usuariosLink);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario es null")
    public void testToModel_WithNullUsuario() {
        assertThrows(IllegalArgumentException.class, () -> assembler.toModel(null));
    }

    @Test
    @DisplayName("Debería mantener todos los datos del usuario")
    public void testToModel_PreservesAllData() {
        EntityModel<UsuarioModel> entityModel = assembler.toModel(usuario);
        UsuarioModel contenido = entityModel.getContent();
        assertEquals(usuarioId, contenido.getId());
        assertEquals("Juan Perez", contenido.getNombre());
        assertEquals("juan@correo.com", contenido.getEmail());
    }
} 