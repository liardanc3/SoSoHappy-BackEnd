package dev.sosohappy.monolithic.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import dev.sosohappy.monolithic.util.FirebaseCredentialResource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FCMConfig {

    @Value("${fcm.type}")
    private String type;

    @Value("${fcm.project_id}")
    private String project_id;

    @Value("${fcm.private_key_id}")
    private String private_key_id;

    @Value("${fcm.private_key}")
    private String private_key;

    @Value("${fcm.client_email}")
    private String client_email;

    @Value("${fcm.client_id}")
    private String client_id;

    @Value("${fcm.auth_uri}")
    private String auth_uri;

    @Value("${fcm.token_uri}")
    private String token_uri;

    @Value("${fcm.auth_provider_x509_cert_url}")
    private String auth_provider_x509_cert_url;

    @Value("${fcm.client_x509_cert_url}")
    private String client_x509_cert_url;

    @Value("${fcm.universe_domain}")
    private String universe_domain;

    private InputStream firebaseInputStream;

    @PostConstruct
    void makeFirebaseInputStream() throws JsonProcessingException {
        FirebaseCredentialResource firebaseCredentialResource = FirebaseCredentialResource.builder()
                .type(type)
                .project_id(project_id)
                .private_key_id(private_key_id)
                .private_key(private_key)
                .client_email(client_email)
                .client_id(client_id)
                .auth_uri(auth_uri)
                .token_uri(token_uri)
                .auth_provider_x509_cert_url(auth_provider_x509_cert_url)
                .client_x509_cert_url(client_x509_cert_url)
                .universe_domain(universe_domain)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(firebaseCredentialResource);

        this.firebaseInputStream = new ByteArrayInputStream(json.getBytes());
    }

    @Bean
    FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(firebaseInputStream))
                .build();

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        return FirebaseMessaging.getInstance(firebaseApp());
    }
}