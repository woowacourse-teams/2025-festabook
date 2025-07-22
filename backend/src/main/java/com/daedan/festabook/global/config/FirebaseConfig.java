package com.daedan.festabook.global.config;

import com.daedan.festabook.global.exception.BusinessException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.adminsdk.account.path}")
    private String firebaseAccountPath;

    @Bean
    public FirebaseApp firebaseApp() {
        try (InputStream serviceAccount = new ClassPathResource(firebaseAccountPath).getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            throw new BusinessException("Firebase 초기화에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
