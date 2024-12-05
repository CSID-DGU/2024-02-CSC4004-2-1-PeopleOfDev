package com.api.momentup.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;

@Component
public class FirebaseInitialization implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/TalkFile_momentup-a2b31-firebase-adminsdk-4c3fn-8d8d98fa76.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) { //<-- 이 조건으로 중복 초기화 방지
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase has been initialized");
        }
    }
}
