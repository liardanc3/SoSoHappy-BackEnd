package sosohappy.authservice.model.dto;

import lombok.Data;

@Data
public class TokenResponseDto {

    private String access_token;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String token_type;

}
