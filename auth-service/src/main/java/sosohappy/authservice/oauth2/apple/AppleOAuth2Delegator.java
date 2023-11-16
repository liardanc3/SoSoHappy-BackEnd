package sosohappy.authservice.oauth2.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import sosohappy.authservice.entity.TokenResponseDto;
import sosohappy.authservice.exception.custom.ForbiddenException;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleOAuth2Delegator {

    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.apple.keyId}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registration.apple.teamId}")
    private String teamId;

    @Value("${spring.security.oauth2.client.registration.apple.secretKey}")
    private String secretKey;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    public String getAppleRefreshToken(String authorizationCode){

        byte[] decodedBytes = Base64.getDecoder().decode(authorizationCode);
        String utf8AuthorizationCode = new String(decodedBytes, StandardCharsets.UTF_8);

        String clientSecret = createClientSecret();
        String tokenURI = "https://appleid.apple.com/auth/token";
        String grantType = "authorization_code";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            add("code", utf8AuthorizationCode);
            add("client_id", clientId);
            add("client_secret", clientSecret);
            add("grant_type", grantType);
            add("redirect_uri", "https://sosohappy.dev/auth-service/login/oauth2/code/apple");
        }};

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<TokenResponseDto> response = restTemplate.postForEntity(tokenURI, httpEntity, TokenResponseDto.class);
            log.info(response.toString());

            if(response.getStatusCode().is2xxSuccessful()){
                return Objects.requireNonNull(response.getBody()).getRefresh_token();
            } else {
                throw new ForbiddenException();
            }

        } catch (HttpClientErrorException e) {
            log.info(e.getMessage());
            log.info(e.getResponseBodyAsString());
            throw new ForbiddenException();
        }

    }

    public boolean handleAppleUserResign(String appleRefreshToken) {
        String clientSecret = createClientSecret();
        String revokeURI = "https://appleid.apple.com/auth/revoke";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(){{
            add("client_id", clientId);
            add("client_secret", clientSecret);
            add("token", appleRefreshToken);
            add("token_type_hint", "refresh_token");
        }};

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(revokeURI, httpEntity, String.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            System.out.println("e.getResponseBodyAsString() = " + e.getResponseBodyAsString());
            throw new ForbiddenException();
        }
    }


    private String createClientSecret() {
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

        byte[] privateKeyBytes = Base64.getDecoder().decode(secretKey);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");

        return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
