package sosohappy.authservice.model.entity;

import jakarta.persistence.*;
import lombok.*;
import sosohappy.authservice.kafka.KafkaProducer;
import sosohappy.authservice.model.dto.UserRequestDto;

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

    @KafkaProducer(topic = "emailAndNickname")
    @SneakyThrows
    public List<String> updateProfile(UserRequestDto userRequestDto)  {
        boolean nicknameEdited = false;
        String originNickname = this.nickname;

        if(userRequestDto.getProfileImg() != null){
            this.profileImg = userRequestDto.getProfileImg().getBytes();
        }
        if(userRequestDto.getNickname() != null){
            this.nickname = userRequestDto.getNickname();
            if(!this.nickname.equals(originNickname)){
                nicknameEdited = true;
            }
        }
        if(userRequestDto.getIntroduction() != null){
            this.introduction = userRequestDto.getIntroduction();
        }

        return List.of(nicknameEdited ? email : "", nickname);
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
