package dev.sosohappy.monolithic.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailDto {

    @Email
    private String email;
}
