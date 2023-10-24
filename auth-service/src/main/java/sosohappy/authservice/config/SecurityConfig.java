package sosohappy.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import sosohappy.authservice.oauth2.converter.CustomRequestEntityConverter;
import sosohappy.authservice.oauth2.handler.OAuth2LoginFailureHandler;
import sosohappy.authservice.oauth2.handler.OAuth2LoginSuccessHandler;
import sosohappy.authservice.oauth2.service.CustomOAuth2UserService;
import sosohappy.authservice.jwt.service.JwtService;
import sosohappy.authservice.jwt.filter.JwtFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final Environment environment;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionConfigurer -> sessionConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(loginConfigurer -> loginConfigurer
                        .tokenEndpoint(tokenEndpointConfig -> tokenEndpointConfig
                                .accessTokenResponseClient(accessTokenResponseClient())
                        )
                        .userInfoEndpoint(userEndpointConfig -> userEndpointConfig
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .addFilterAfter(jwtFilter, LogoutFilter.class)
                .build();
    }

    @Bean
    public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
        accessTokenResponseClient.setRequestEntityConverter(new CustomRequestEntityConverter(environment));

        return accessTokenResponseClient;
    }
}

