package com.pagos.pagos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.UsuarioModel;
import com.pagos.pagos.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioModel> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioModel> findById(UUID id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioModel save(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(UUID id) {
        usuarioRepository.deleteById(id);
    }
}