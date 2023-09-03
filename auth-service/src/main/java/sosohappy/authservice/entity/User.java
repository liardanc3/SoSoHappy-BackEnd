package sosohappy.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Base64;

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
    private String profileImg;

    @Column
    private String provider;
    @Column
    private String providerId;

    @Column
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User updateProfile(UserDto userDto){
        this.profileImg = userDto.getProfileImg();
        this.nickname = userDto.getNickname();
        this.introduction = userDto.getIntroduction();

        return this;
    }
}
