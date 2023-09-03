package sosohappy.authservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DuplicateDto {

    private String email;
    private Boolean isPresent;
}
