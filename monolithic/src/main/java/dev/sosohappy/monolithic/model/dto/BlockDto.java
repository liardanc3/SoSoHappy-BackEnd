package dev.sosohappy.monolithic.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BlockDto {

    @NotEmpty
    private String srcNickname;

    @NotEmpty
    private String dstNickname;
}
