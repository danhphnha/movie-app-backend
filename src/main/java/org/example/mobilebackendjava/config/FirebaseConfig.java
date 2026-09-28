package org.example.mobilebackendjava.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp initFirebase() throws IOException {
        String firebaseConfigEnv = System.getenv("FIREBASE_CONFIG");
        FirebaseOptions options;
        String databaseUrl = "https://movieapp-f0c63.firebaseio.com";

        if (firebaseConfigEnv != null && !firebaseConfigEnv.trim().isEmpty()) {
            log.info("Initializing Firebase from FIREBASE_CONFIG environment variable (Base64)");
            byte[] decoded = Base64.getDecoder().decode(firebaseConfigEnv.trim());
            try (ByteArrayInputStream serviceAccount = new ByteArrayInputStream(decoded)) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setDatabaseUrl(databaseUrl)
                        .build();
            }
        } else {
            String defaultFilePath = "src/main/resources/movieapp-f0c63-0f983a1aa75c.json";
            File file = new File(defaultFilePath);
            if (file.exists()) {
                log.info("Initializing Firebase from local service account file: {}", defaultFilePath);
                try (InputStream serviceAccount = new FileInputStream(file)) {
                    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                    options = FirebaseOptions.builder()
                            .setCredentials(credentials)
                            .setDatabaseUrl(databaseUrl)
                            .build();
                }
            } else {
                log.warn("No specific Firebase credential found in env 'FIREBASE_CONFIG' or 'src/main/resources/movieapp-f0c63-0f983a1aa75c.json'.");
                try {
                    options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.getApplicationDefault())
                            .setDatabaseUrl(databaseUrl)
                            .build();
                } catch (IOException e) {
                    throw new IllegalStateException(
                            "❌ Firebase credentials not configured! Please provide your service account key:\n" +
                            "  👉 Cách 1: Thêm biến môi trường FIREBASE_CONFIG (chuỗi Base64 của file JSON key) vào IntelliJ Run Configuration.\n" +
                            "  👉 Cách 2: Copy file key JSON vào đường dẫn 'src/main/resources/movieapp-f0c63-0f983a1aa75c.json'.",
                            e
                    );
                }
            }
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            log.info("FirebaseApp initialized successfully");
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    public Firestore getFirestore(FirebaseApp firebaseApp) {
        return FirestoreClient.getFirestore(firebaseApp);
    }
}
