package sosohappy.authservice.oauth2.attributes;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private String email;
    private String provider;
    private String providerId;

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        if (provider.equals("kakao")) {
            return ofKakao(attributes);
        }
        if (provider.equals("google")){
            return ofGoogle(attributes);
        }
        return ofApple(attributes);
    }

    public Map<String, Object> attributes(){
        return Map.of(
                "email", email,
                "provider", provider,
                "providerId", providerId
        );
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider("kakao")
                .providerId(attributes.get("id").toString())
                .email(((Map<String, Object>) attributes.get("kakao_account")).get("email").toString())
                .build();
    }

    private static OAuthAttributes ofApple(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider("apple")
                .providerId(attributes.get("sub").toString())
                .email(attributes.get("email").toString())
                .build();
    }

    public static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .provider("google")
                .providerId(attributes.get("sub").toString())
                .email(attributes.get("email").toString())
                .build();
    }

}