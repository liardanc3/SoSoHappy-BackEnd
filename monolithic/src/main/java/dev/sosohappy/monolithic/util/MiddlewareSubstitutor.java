package dev.sosohappy.monolithic.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Middleware Substitutor
 */
@Component
public class MiddlewareSubstitutor {

    public final ConcurrentHashMap<String, String> emailAndDeviceTokenMap = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, String> emailAndNicknameMap = new ConcurrentHashMap<>();

    public void produceEmailAndNickname(String email, String nickname) {
        emailAndNicknameMap.put(email, nickname);
    }

    public void produceDeviceToken(String email, String deviceToken){
        emailAndDeviceTokenMap.put(email, deviceToken);
    }

    public void produceResign(String email, String nickname) {

    }
}
