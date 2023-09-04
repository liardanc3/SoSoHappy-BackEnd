package sosohappy.authservice.entity;

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

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @SneakyThrows
    public void updateProfile(UserRequestDto userRequestDto)  {
        this.profileImg = userRequestDto.getProfileImg().getBytes();
        this.nickname = userRequestDto.getNickname();
        this.introduction = userRequestDto.getIntroduction();
    }
}
