package sosohappy.authservice.oauth2;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private String email;
    private String name;
    private String provider;
    private String providerId;

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {

        if (provider.equals("kakao")) {
            return ofKakao(attributes);
        }

        return ofGoogle(attributes);
    }

    public Map<String, Object> attributes(){
        return Map.of(
                "email", email,
                "name", name,
                "provider", provider,
                "providerId", providerId
        );
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider("kakao")
                .providerId(attributes.get("id").toString())
                .email(((Map<String, Object>) attributes.get("kakao_account")).get("email").toString())
                .name(((Map<String, Object>) attributes.get("properties")).get("nickname").toString())
                .build();
    }


    public static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider("google")
                .providerId(attributes.get("sub").toString())
                .email(attributes.get("email").toString())
                .name(attributes.get("name").toString())
                .build();
    }

}