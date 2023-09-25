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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomRequestEntityConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, RequestEntity<?>> {

    private final OAuth2AuthorizationCodeGrantRequestEntityConverter converter;

    @Override
    public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest request) {
        RequestEntity<?> entity = converter.convert(request);
        String provider = request.getClientRegistration().getRegistrationId();
        MultiValueMap<String, String> parameterMap = (MultiValueMap<String, String>) entity.getBody();

        if(provider.contains("apple")){
            parameterMap.set("client_secret",
                    createClientSecret(
                            parameterMap.get("client_id").get(0),
                            parameterMap.get("client_secret").get(0),
                            parameterMap.get("key_id").get(0),
                            parameterMap.get("team_id").get(0)
                    )
            );
        }

        return new RequestEntity<>(parameterMap, entity.getHeaders(), entity.getMethod(), entity.getUrl());
    }

    private String createClientSecret(String clientId, String clientSecret, String keyId, String teamId) {
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

        return  new JcaPEMKeyConverter().getPrivateKey(pemKeyPair.getPrivateKeyInfo());
    }
}
