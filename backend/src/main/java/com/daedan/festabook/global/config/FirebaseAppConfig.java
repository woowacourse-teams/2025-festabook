package com.daedan.festabook.global.config;

import com.daedan.festabook.global.exception.BusinessException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class FirebaseAppConfig {

    private final ResourceLoader resourceLoader;

    @Value("${firebase.adminsdk.account.path}")
    private String firebaseAccountPath;

    @Bean
    public FirebaseApp firebaseApp() {
        Resource resource = resourceLoader.getResource(firebaseAccountPath);
        try (InputStream serviceAccount = resource.getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();
        } catch (IOException e) {
            throw new BusinessException("Firebase 초기화에 실패했습니다." + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
