package sosohappy.authservice.entity;

import lombok.Data;

@Data
public class UserDto {

    private String email;
    private String profileImg;

    private String nickname;
    private String introduction;
}
