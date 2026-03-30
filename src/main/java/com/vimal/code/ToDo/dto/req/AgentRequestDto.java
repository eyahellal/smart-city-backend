package com.vimal.code.ToDo.dto.req;

import com.vimal.code.ToDo.models.Role;
import com.vimal.code.ToDo.models.ServiceType;
import com.vimal.code.ToDo.models.ServiceUrbain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentRequestDto {

    private String name;
    private String email;
    private String password;
    private ServiceType serviceType;
    private Role role;
}
