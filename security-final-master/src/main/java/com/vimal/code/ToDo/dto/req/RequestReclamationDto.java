package com.vimal.code.ToDo.dto.req;

import com.vimal.code.ToDo.models.ServiceType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@Data
public class RequestReclamationDto {

    private MultipartFile image;
    private String description;
    private ServiceType serviceType;
    private double latitude;
    private double longitude;
}
