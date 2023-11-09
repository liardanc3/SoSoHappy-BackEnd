package sosohappy.authservice.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SignInDto {

    @NotEmpty
    private String codeVerifier;

    @NotEmpty
    private String authorizeCode;

    @NotEmpty @Email
    private String email;

    @NotEmpty
    private String provider;

    @NotEmpty
    private String providerId;

    private String authorizationCode;
}
