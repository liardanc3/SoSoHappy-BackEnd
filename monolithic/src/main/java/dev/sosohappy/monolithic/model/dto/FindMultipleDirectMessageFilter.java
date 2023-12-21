package dev.sosohappy.monolithic.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindMultipleDirectMessageFilter {

    @NotEmpty
    private String sender;
}
