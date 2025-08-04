package com.daedan.festabook.global.config;

import com.google.firebase.FirebaseApp;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MockFirebaseAppConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        return Mockito.mock(FirebaseApp.class);
    }
}
