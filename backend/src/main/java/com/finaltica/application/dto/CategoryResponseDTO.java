package com.finaltica.application.dto;

import java.time.Instant;
import java.util.UUID;

import com.finaltica.application.enums.CategoryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private UUID id;
    
    private String name;
    
    private CategoryType type; // INCOME or EXPENSE
    
    private boolean isGlobal; // true = system category, false = user's custom
    
    private Instant createdAt;
    
    private Instant updatedAt;
}