package sosohappy.authservice.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserRequestDto {

    private String email;
    private MultipartFile profileImg;

    private String nickname;
    private String introduction;
}
