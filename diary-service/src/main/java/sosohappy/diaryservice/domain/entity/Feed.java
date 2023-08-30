package sosohappy.diaryservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_id")
    private Long id;

    @Column
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column
    private Weather weather;

    @Column
    private Long date;

    @Column
    private Integer happiness;

    @Column
    private String text;

    @Column
    private Boolean isPublic;

    @ElementCollection
    @CollectionTable(
            name = "feed_categories",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> categoryList;

    @ElementCollection
    @CollectionTable(
            name = "feed_images",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> imageList; // base64

    @ElementCollection
    @CollectionTable(
            name = "feed_likes",
            joinColumns = @JoinColumn(name = "feed_id")
    )
    @Column
    private List<String> likeNicknameList;
}
