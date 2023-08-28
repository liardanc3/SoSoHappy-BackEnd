package sosohappy.authservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResignDto {

    private Integer httpStatus;
    private String email;
}
