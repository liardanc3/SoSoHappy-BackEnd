package sosohappy.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import sosohappy.authservice.entity.UserRequestDto;
import sosohappy.authservice.kafka.KafkaProducer;

import java.util.List;

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

    @KafkaProducer(topic = "nickname")
    @SneakyThrows
    public List<String> updateProfile(UserRequestDto userRequestDto)  {
        if(userRequestDto.getProfileImg() != null){
            this.profileImg = userRequestDto.getProfileImg().getBytes();
        }
        if(userRequestDto.getNickname() != null){
            this.nickname = userRequestDto.getNickname();
        }
        if(userRequestDto.getIntroduction() != null){
            this.introduction = userRequestDto.getIntroduction();
        }

        return List.of(email, nickname);
    }

    public void updateAppleRefreshToken(String appleRefreshToken){
        this.appleRefreshToken = appleRefreshToken;
    }

    @KafkaProducer(topic = "deviceToken")
    public List<String> updateDeviceToken(String deviceToken){
        this.deviceToken = deviceToken;

        return List.of(email, deviceToken);
    }
}
