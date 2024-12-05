package com.api.momentup.service;

import com.api.momentup.domain.NotificationType;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FcmService {
    /**
     * 안드로이드 앱에 FCM 알림을 보내는 메소드.
     *
     * @param title 메시지 제목
     * @param body 메시지 내용
     * @param token 대상 디바이스 토큰
     * @param notificationType 알림 유형
     * @param number 알림과 관련된 식별 번호
     */
    public void sendMessageToToken(String title, String body, String token, NotificationType notificationType, Long number) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        // 메시지 구성
        Message message = Message.builder()
                .setNotification(notification) // 알림 내용 설정
                .putData("type", notificationType.toString())    // 추가 데이터: 알림 유형
                .putData("id", number.toString())                // 추가 데이터: 식별 번호
                .setToken(token)                                // 대상 토큰
                .build();

        try {
            // 메시지를 FCM을 통해 전송하고 결과를 출력
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.err.println("Failed to send FCM message: " + e.getMessage());
        }
    }
}
