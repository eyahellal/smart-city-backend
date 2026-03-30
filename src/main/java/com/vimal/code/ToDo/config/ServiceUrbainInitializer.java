package com.vimal.code.ToDo.config;


import com.vimal.code.ToDo.Repositories.ServiceUrbainRepository;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ServiceUrbainInitializer implements CommandLineRunner {

    private final ServiceUrbainRepository serviceUrbainRepository;

    public ServiceUrbainInitializer(ServiceUrbainRepository serviceUrbainRepository) {
        this.serviceUrbainRepository = serviceUrbainRepository;
    }

    @Override
    public void run(String... args) {
        List<ServiceType> services = Arrays.asList(ServiceType.MAIRIE, ServiceType.POLICE_LOCALE, ServiceType.POLICE_MUNICIPALE,ServiceType.STEG,ServiceType.SONEDE);

        }
    }

