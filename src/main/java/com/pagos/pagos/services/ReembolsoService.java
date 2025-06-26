package com.pagos.pagos.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagos.pagos.model.ReembolsoModel;
import com.pagos.pagos.repository.ReembolsoRepository;

@Service
public class ReembolsoService {

    private final ReembolsoRepository reembolsoRepository;

    @Autowired
    public ReembolsoService(ReembolsoRepository reembolsoRepository) {
        this.reembolsoRepository = reembolsoRepository;
    }

    public List<ReembolsoModel> findAll() {
        return reembolsoRepository.findAll();
    }

    public Optional<ReembolsoModel> findById(UUID id) {
        return reembolsoRepository.findById(id);
    }

    public ReembolsoModel save(ReembolsoModel reembolso) {
        return reembolsoRepository.save(reembolso);
    }

    public void deleteById(UUID id) {
        reembolsoRepository.deleteById(id);
    }
}