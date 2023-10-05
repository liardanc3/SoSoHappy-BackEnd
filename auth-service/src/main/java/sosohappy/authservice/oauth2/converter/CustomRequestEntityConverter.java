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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
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
import java.util.Set;

@Component
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private OAuth2AuthorizationCodeGrantRequestEntityConverter converter;

    private final String keyId;
    private final String teamId;
    private final String clientSecret;

    public CustomRequestEntityConverter(Environment environment) {
        this.converter = new OAuth2AuthorizationCodeGrantRequestEntityConverter();

        this.keyId = environment.getProperty("auth.keyId");
        this.teamId = environment.getProperty("auth.teamId");
        this.clientSecret = environment.getProperty("auth.clientSecret");
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
                    "client_secret", "asdasd"
                    //createClientSecret(parameterMap.getFirst("client_id"))
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
                .sign(Algorithm.ECDSA256((ECPrivateKey) parsePrivateKey()));
    }

    @SneakyThrows
    private PrivateKey parsePrivateKey() {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println("clientSecret = " + clientSecret);
        PEMParser pemParser = new PEMParser(new StringReader(clientSecret));
        System.out.println("pemParser.readObject() = " + pemParser.readObject());
        PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
        Set<String> supportedTypes = pemParser.getSupportedTypes();
        for (String supportedType : supportedTypes) {
            System.out.println("supportedType = " + supportedType);
        }

        return new JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
    }
}
