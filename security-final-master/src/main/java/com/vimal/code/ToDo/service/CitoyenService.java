package com.vimal.code.ToDo.service;


import com.vimal.code.ToDo.Repositories.CitoyenRepository;
import com.vimal.code.ToDo.Repositories.EventRepository;
import com.vimal.code.ToDo.models.Citoyen;
import com.vimal.code.ToDo.Repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitoyenService {

    @Autowired
    private CitoyenRepository citoyenRepository;
    @Autowired
    private UserRepo userRepository;

    public List<Citoyen> getAllCitoyens() {
        return citoyenRepository.findAll();
    }

    public Optional<Citoyen> getCitoyenById(Long id) {
        return citoyenRepository.findById(id);
    }


}
