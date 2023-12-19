package dev.sosohappy.monolithic.model.entity;

import dev.sosohappy.monolithic.model.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;
    @Column
    private String nickname;
    @Column
    private String introduction;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] profileImg;

    @Column
    private String provider;
    @Column
    private String providerId;

    @Column
    private String refreshToken;

    @Column
    private String deviceToken;

    @Column
    private String appleRefreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @SneakyThrows
    public void updateProfile(UserRequestDto userRequestDto)  {
        if(userRequestDto.getProfileImg() != null){
            this.profileImg = userRequestDto.getProfileImg().getBytes();
        }
        if(userRequestDto.getNickname() != null){
            this.nickname = userRequestDto.getNickname();
        }
        if(userRequestDto.getIntroduction() != null){
            this.introduction = userRequestDto.getIntroduction();
        }
    }

    public void updateAppleRefreshToken(String appleRefreshToken){
        this.appleRefreshToken = appleRefreshToken;
    }

    public void updateDeviceToken(String deviceToken){
        this.deviceToken = deviceToken;
    }

    public void updateProviderId(String providerId) {
        this.providerId = providerId;
    }
}
