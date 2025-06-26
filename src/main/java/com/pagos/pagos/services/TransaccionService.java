package com.pagos.pagos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.TransaccionModel;
import com.pagos.pagos.repository.TransaccionRepository;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;

    @Autowired
    public TransaccionService(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    public List<TransaccionModel> findAll() {
        return transaccionRepository.findAll();
    }

    public Optional<TransaccionModel> findById(UUID id) {
        return transaccionRepository.findById(id);
    }

    public TransaccionModel save(TransaccionModel transaccion) {
        return transaccionRepository.save(transaccion);
    }

    public void deleteById(UUID id) {
        transaccionRepository.deleteById(id);
    }
}