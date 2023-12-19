package dev.sosohappy.monolithic.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private String nickname;
    private byte[] profileImg;
}
