package dev.sosohappy.monolithic.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicknameDto {

    @Size(min = 1, max = 100)
    private String nickname;
}
