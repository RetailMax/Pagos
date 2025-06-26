package com.pagos.pagos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.NotificacionModel;
import com.pagos.pagos.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<NotificacionModel> findAll() {
        return notificacionRepository.findAll();
    }

    public Optional<NotificacionModel> findById(UUID id) {
        return notificacionRepository.findById(id);
    }

    public NotificacionModel save(NotificacionModel notificacion) {
        return notificacionRepository.save(notificacion);
    }

    public void deleteById(UUID id) {
        notificacionRepository.deleteById(id);
    }
}
