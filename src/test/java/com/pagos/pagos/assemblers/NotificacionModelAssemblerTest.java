package com.pagos.pagos.assemblers;

import java.time.LocalDateTime;
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

import com.pagos.pagos.model.NotificacionModel;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Ensamblador de Modelo de Notificación")
public class NotificacionModelAssemblerTest {

    @InjectMocks
    private NotificacionModelAssembler assembler;

    private NotificacionModel notificacion;
    private UUID notificacionId;
    private UUID destinatarioId;

    @BeforeEach
    void setUp() {
        notificacionId = UUID.randomUUID();
        destinatarioId = UUID.randomUUID();
        notificacion = new NotificacionModel();
        notificacion.setId(notificacionId);
        notificacion.setDestinatarioId(destinatarioId);
        notificacion.setMensaje("Mensaje de prueba");
        notificacion.setTipo("INFO");
        notificacion.setFechaEnvio(LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería crear EntityModel correctamente")
    public void testToModel() {
        EntityModel<NotificacionModel> entityModel = assembler.toModel(notificacion);
        assertNotNull(entityModel);
        assertEquals(notificacion, entityModel.getContent());
        assertNotNull(entityModel.getLinks());
        assertFalse(entityModel.getLinks().isEmpty());
    }

    @Test
    @DisplayName("Debería contener enlace self")
    public void testToModel_ContainsSelfLink() {
        EntityModel<NotificacionModel> entityModel = assembler.toModel(notificacion);
        assertTrue(entityModel.hasLink("self"));
        Link selfLink = entityModel.getLink("self").orElse(null);
        assertNotNull(selfLink);
        assertTrue(selfLink.getHref().contains(notificacionId.toString()));
    }

    @Test
    @DisplayName("Debería contener enlace a notificaciones")
    public void testToModel_ContainsNotificacionesLink() {
        EntityModel<NotificacionModel> entityModel = assembler.toModel(notificacion);
        assertTrue(entityModel.hasLink("notificaciones"));
        Link notificacionesLink = entityModel.getLink("notificaciones").orElse(null);
        assertNotNull(notificacionesLink);
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la notificación es null")
    public void testToModel_WithNullNotificacion() {
        assertThrows(IllegalArgumentException.class, () -> assembler.toModel(null));
    }

    @Test
    @DisplayName("Debería mantener todos los datos de la notificación")
    public void testToModel_PreservesAllData() {
        EntityModel<NotificacionModel> entityModel = assembler.toModel(notificacion);
        NotificacionModel contenido = entityModel.getContent();
        assertEquals(notificacionId, contenido.getId());
        assertEquals(destinatarioId, contenido.getDestinatarioId());
        assertEquals("Mensaje de prueba", contenido.getMensaje());
        assertEquals("INFO", contenido.getTipo());
    }
} 