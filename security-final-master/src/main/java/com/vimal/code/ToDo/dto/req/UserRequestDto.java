package com.vimal.code.ToDo.dto.req;

import com.vimal.code.ToDo.models.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

        private String name;
        private String email;
        private String password;
        private String state;
        private String city;
        private Role role;


}
