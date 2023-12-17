package dev.sosohappy.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka Substitutor
 */
@Component
public class KeyValueMemoryDB {

    public final ConcurrentHashMap<String, String> emailAndDeviceTokenMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, String> emailAndNicknameMap = new ConcurrentHashMap<>();

    public void produceEmailAndNickname(String email, String nickname) {
        emailAndNicknameMap.put(email, nickname);
    }

    public void produceDeviceToken(String email, String deviceToken){
        emailAndDeviceTokenMap.put(email, deviceToken);
    }
}
