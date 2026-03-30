package com.vimal.code.ToDo.service;

import com.vimal.code.ToDo.Repositories.AgentRepository;
import com.vimal.code.ToDo.Repositories.ServiceUrbainRepository;
import com.vimal.code.ToDo.dto.req.AgentRequestDto;
import com.vimal.code.ToDo.dto.resp.AgentResponseDto;
import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.models.Agent;
import com.vimal.code.ToDo.models.ServiceUrbain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;


    @Autowired
    private ServiceUrbainRepository serviceUrbainRepository;

    public AgentResponseDto createAgent(AgentRequestDto agentRequestDto) {
        Agent agent = new Agent();
        agent.setName(agentRequestDto.getName());
        agent.setEmail(agentRequestDto.getEmail());
        String hashedPassword = passwordEncoder.encode(agentRequestDto.getPassword());
        agent.setPassword(hashedPassword);
        agent.setRole(Role.AGENT);
        ServiceUrbain serviceUrbain = serviceUrbainRepository.findByType(agentRequestDto.getServiceType());

        // Associe l'agent au service urbain récupéré
        agent.setServiceUrbain(serviceUrbain);

        // Sauvegarde l'agent en base de données
        Agent savedAgent = agentRepository.save(agent);

        // Retourne la réponse
        return new AgentResponseDto(savedAgent.getName(), savedAgent.getEmail(), savedAgent.getServiceUrbain(), savedAgent.getId());
    }

    public Optional<Agent> findByEmail(String agentEmail) {
        return agentRepository.findAgentByEmail(agentEmail) ;  }
}