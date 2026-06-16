package com.example.dash.common.file.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageTask {
    private String storedPath;
    private String type;
    private String entityType;
    private String entityId;
}
