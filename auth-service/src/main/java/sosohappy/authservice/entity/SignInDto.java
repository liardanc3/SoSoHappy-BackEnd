package sosohappy.authservice.entity;

import lombok.Data;

@Data
public class SignInDto {

    private String codeVerifier;
    private String authorizeCode;
    private String email;
    private String provider;
    private String providerId;

}
