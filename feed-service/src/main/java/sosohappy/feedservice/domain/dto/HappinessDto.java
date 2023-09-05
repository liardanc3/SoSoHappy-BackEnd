package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

@Data
public class HappinessDto {

    private Integer happiness;
    private List<String> categories;

    public HappinessDto(Feed feed) {
        this.happiness = feed.getHappiness();
        this.categories = feed.getCategoryList();
    }
}
