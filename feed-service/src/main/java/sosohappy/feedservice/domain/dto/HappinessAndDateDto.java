package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;

@Data
public class HappinessAndDateDto {

    private Integer happiness;
    private String formattedDate;

    public HappinessAndDateDto(Feed feed){
        this.happiness = feed.getHappiness();
        this.formattedDate = feed.getDate().toString().substring(4,6) + "/" + feed.getDate().toString().substring(6,8);
    }
}
