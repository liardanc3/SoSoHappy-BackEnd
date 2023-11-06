package sosohappy.authservice.oauth2.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import sosohappy.authservice.oauth2.attributes.OAuthAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * deprecated
 * <br>
 * https://support.google.com/faqs/answer/12284343?hl=ko
 */

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String provider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = new HashMap<>();

        if(provider.contains("apple")){
            String idToken = userRequest.getAdditionalParameters().get("id_token").toString();
            attributes.putAll(decodeAppleToken(idToken));
        } else {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            attributes.putAll(oAuth2User.getAttributes());
        }

        HashMap<String, Object> updatedAttributes = new HashMap<>(){{
            putAll(attributes);
            putAll(OAuthAttributes.of(provider, attributes).attributes());
        }};

        return new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(updatedAttributes)),
                updatedAttributes,
                "providerId"
        );
    }

    @SneakyThrows
    private Map<String, Object> decodeAppleToken(String idToken) {
        String[] parts = idToken.split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();

        byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();

        return (Map<String, Object>) mapper.readValue(decodedString, Map.class);
    }

}
