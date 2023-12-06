package sosohappy.feedservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FeedLikeNickname {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_like_nickname_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column
    private String nickname;

    public FeedLikeNickname(Feed feed, String nickname) {
        this.feed = feed;
        this.nickname = nickname;
    }
}
