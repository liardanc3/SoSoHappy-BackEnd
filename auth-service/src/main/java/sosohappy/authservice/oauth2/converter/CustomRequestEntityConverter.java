package sosohappy.authservice.oauth2.converter;

// Reference : https://devcheon.tistory.com/98

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.Map;

@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private OAuth2AuthorizationCodeGrantRequestEntityConverter converter;

    @Value("${auth.key-id}")
    private String keyId;

    @Value("${auth.team-id}")
    private String teamId;

    @Value("${auth.client-secret}")
    private String clientSecret;

    public CustomRequestEntityConverter() {
        this.converter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(@NotNull OAuth2AuthorizationCodeGrantRequest request) {
        RequestEntity<?> entity = converter.convert(request);
        String provider = request.getClientRegistration().getRegistrationId();
        System.out.println("entity = " + entity);
        System.out.println("entity.getType() = " + entity.getType());
        System.out.println("entity.toString() = " + entity.toString());
        Object body = entity.getBody();
        System.out.println("body.toString() = " + body.toString());

        LinkedMultiValueMap<String, String> parameterMap = (LinkedMultiValueMap<String, String>) entity.getBody();

        System.out.println("provider = " + provider);
        if(provider.contains("apple")){
            parameterMap.set(
                    "client_secret",
                    createClientSecret(parameterMap.getFirst("client_id"))
            );
        }

        return new RequestEntity<>(parameterMap, entity.getHeaders(), entity.getMethod(), entity.getUrl());
    }

    private String createClientSecret(String clientId) {
        System.out.println("clientId = " + clientId);
        System.out.println("clientSecret = " + clientSecret);
        System.out.println("keyId = " + keyId);
        System.out.println("teamId = " + teamId);
        return JWT.create()
                .withHeader(Map.of("kid", keyId))
                .withIssuer(teamId)
                .withAudience("https://appleid.apple.com")
                .withSubject(clientId)
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60)))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(Algorithm.ECDSA256((ECPrivateKey) parsePrivateKey(clientSecret)));
    }

    @SneakyThrows
    private static PrivateKey parsePrivateKey(String clientSecret) {
        Security.addProvider(new BouncyCastleProvider());
        PEMParser pemParser = new PEMParser(new StringReader(clientSecret));
        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();

        return new JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
    }
}
