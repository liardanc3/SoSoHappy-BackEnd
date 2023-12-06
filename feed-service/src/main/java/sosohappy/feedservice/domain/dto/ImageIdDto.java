package sosohappy.feedservice.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageIdDto {

    @NotNull
    private Long imageId;
}
