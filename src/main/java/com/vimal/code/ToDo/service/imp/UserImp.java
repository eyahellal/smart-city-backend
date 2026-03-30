package com.vimal.code.ToDo.service.imp;

import com.vimal.code.ToDo.Repositories.*;
import com.vimal.code.ToDo.config.AuthConfig;
import com.vimal.code.ToDo.dto.req.AgentRequestDto;
import com.vimal.code.ToDo.dto.req.UserRequestDto;
import com.vimal.code.ToDo.dto.resp.UserResponseDto;
import com.vimal.code.ToDo.models.*;
import com.vimal.code.ToDo.exp.UserAlreadyExistsException;
import com.vimal.code.ToDo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserImp implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CitoyenRepository citoyenRepo;
    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ServiceUrbainRepository serviceUrbainRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthConfig authConfig;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEnitiy user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        System.out.println("Retrieved Data");
        System.out.println(user.getPassword() + " Retrieved Password");
        System.out.println(user.getUsername());
        System.out.println(user.getId());
        System.out.println(user.getEmail());
        System.out.println("-----");
        return user;
    }

    @Override
    public List<UserResponseDto> getAllUser() {
        List<UserEnitiy> userEnitiys = userRepo.findAll();
        List<UserResponseDto> userResponseDtoList = userEnitiys.stream()
                .map(this::userEntityToUserRespDto)
                .collect(Collectors.toList());
        return userResponseDtoList;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        Optional<UserEnitiy> foundUser = this.userRepo.findByEmail(userRequestDto.getEmail());

        if (foundUser.isEmpty()) {
            Citoyen citoyen = new Citoyen();
            citoyen.setName(userRequestDto.getName());
            citoyen.setEmail(userRequestDto.getEmail());
            citoyen.setPassword(authConfig.passwordEncoder().encode(userRequestDto.getPassword()));

            citoyen.setRole(Role.CITOYEN);

            citoyen.setCity(userRequestDto.getCity());
            citoyen.setState(userRequestDto.getState());
            Citoyen createdCitoyen = citoyenRepo.save(citoyen);

            return this.userEntityToUserRespDto(createdCitoyen);
        } else {
            throw new UserAlreadyExistsException("User with email " + userRequestDto.getEmail() + " already exists");
        }
    }


    public UserEnitiy userReqDtoToUserEntity(UserRequestDto userReqDto) {
        UserEnitiy user = this.modelMapper.map(userReqDto, UserEnitiy.class);
        return user;
    }

    public UserResponseDto userEntityToUserRespDto(UserEnitiy user) {
        UserResponseDto userRespDto = this.modelMapper.map(user, UserResponseDto.class);
        return userRespDto;
    }
    public UserEnitiy findByEmail(String email) {
        Optional<UserEnitiy> citoyen = userRepo.findByEmail(email);
        return citoyen.orElse(null); // Returns null if not found (or throw an exception
    }
    public UserResponseDto createAgent(AgentRequestDto agentRequestDto) {
        Optional<UserEnitiy> foundUser = this.userRepo.findByEmail(agentRequestDto.getEmail());

        if (foundUser.isEmpty()) {
            // Create an Agent entity
            Agent agent = new Agent();
            agent.setName(agentRequestDto.getName());
            agent.setEmail(agentRequestDto.getEmail());
            agent.setPassword(authConfig.passwordEncoder().encode(agentRequestDto.getPassword()));

            // Assign role to Agent
            agent.setRole(Role.AGENT);

            ServiceUrbain service = serviceUrbainRepository.findByType(agentRequestDto.getServiceType());
            if (service == null) {
                throw new EntityNotFoundException("ServiceUrbain not found for type: " + agentRequestDto.getServiceType());
            }            agent.setServiceUrbain(service);

            // Save the Agent entity (assuming userRepo can save Agent entities)
            UserEnitiy createdAgent = userRepo.save(agent);

            return this.userEntityToUserRespDto(createdAgent);
        } else {
            throw new UserAlreadyExistsException("Agent with email " + agentRequestDto.getEmail() + " already exists");
        }
    }

    @Override
    public UserResponseDto updateCitoyen(long id, UserRequestDto userRequestDto) {
        Citoyen user = citoyenRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setState(userRequestDto.getState());
        user.setCity(userRequestDto.getCity());
        // Add other fields if necessary (phone number, address, etc.)

        Citoyen updatedUser = citoyenRepo.save(user);

        // 🛠 Manually create a ResponseDto (no mapper needed)
        return this.userEntityToUserRespDto(user);


    }
    public List<Agent> findAllAgents() {
        return agentRepository.findAll();
    }

    }
