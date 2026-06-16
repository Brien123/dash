package com.example.dash.common.Email.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailTask {
    private String to;
    private String code;
    private String type;
}
