package sosohappy.authservice.oauth2.converter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final OAuth2AuthorizationCodeGrantRequestEntityConverter converter;

    private final String keyId;
    private final String teamId;
    private final String clientSecret;
    private final String clientId;

    public CustomRequestEntityConverter(Environment environment) {
        this.converter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

        this.keyId = environment.getProperty("spring.security.oauth2.client.registration.apple.keyId");
        this.teamId = environment.getProperty("spring.security.oauth2.client.registration.apple.teamId");
        this.clientSecret = environment.getProperty("spring.security.oauth2.client.registration.apple.clientSecret");
        this.clientId = environment.getProperty("spring.security.oauth2.client.registration.apple.clientId");
    }

    @Override
    public RequestEntity<?> convert(@NotNull OAuth2AuthorizationCodeGrantRequest request) {
        RequestEntity<?> entity = converter.convert(request);
        String provider = request.getClientRegistration().getRegistrationId();

        LinkedMultiValueMap<String, String> parameterMap = (LinkedMultiValueMap<String, String>) entity.getBody();

        if(provider.contains("apple")){
            parameterMap.set(
                    "client_secret", createClientSecret()
            );
        }

        return new RequestEntity<>(parameterMap, entity.getHeaders(), entity.getMethod(), entity.getUrl());
    }

    public String createClientSecret() {
        return JWT.create()
                .withHeader(Map.of("kid", keyId))
                .withIssuer(teamId)
                .withAudience("https://appleid.apple.com")
                .withSubject(clientId)
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60)))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(Algorithm.ECDSA256(parsePrivateKey()));
    }

    @SneakyThrows
    private ECPrivateKey parsePrivateKey() {
        Security.addProvider(new BouncyCastleProvider());

        byte[] privateKeyBytes = Base64.getDecoder().decode(clientSecret);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");

        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
