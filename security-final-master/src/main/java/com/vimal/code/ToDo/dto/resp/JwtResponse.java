package com.vimal.code.ToDo.dto.resp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JwtResponse {
    private String token;
    private String name;
    private String role;
    private String id;
    private String city;
    private String state;

}
