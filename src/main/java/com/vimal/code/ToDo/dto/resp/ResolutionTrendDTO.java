package com.vimal.code.ToDo.dto.resp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ResolutionTrendDTO {
    private String date;
    private int resolvedCount;
}