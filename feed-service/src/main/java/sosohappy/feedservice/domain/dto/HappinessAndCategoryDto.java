package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;

@Data
public class HappinessAndCategoryDto {

    private Integer happiness;
    private List<String> categories;

    public HappinessAndCategoryDto(Feed feed) {
        this.happiness = feed.getHappiness();
        this.categories = feed.getCategoryList();
    }
}
