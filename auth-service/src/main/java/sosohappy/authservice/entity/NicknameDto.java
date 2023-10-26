package sosohappy.authservice.entity;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameDto {

    @Size(min = 1, max = 10)
    private String nickname;
}
