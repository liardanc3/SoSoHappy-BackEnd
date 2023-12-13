package sosohappy.feedservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FeedCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feed_category_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id")
    private Feed feed;

    @Column
    private String category;

    public FeedCategory(Feed feed, String category) {
        this.feed = feed;
        this.category = category;
    }

    public void deleteRelationship(){
        this.feed = null;
    }
}
