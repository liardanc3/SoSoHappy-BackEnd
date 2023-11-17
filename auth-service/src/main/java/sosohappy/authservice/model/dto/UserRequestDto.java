package sosohappy.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserRequestDto {

    @NotEmpty @Email
    private String email;

    @NotEmpty
    private String nickname;

    private MultipartFile profileImg;
    private String introduction;
}
