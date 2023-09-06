package sosohappy.feedservice.domain.dto;

import lombok.Data;
import sosohappy.feedservice.domain.entity.Feed;

@Data
public class MonthHappinessAndDateDto {

    private Integer happiness;
    private String formattedDate;

    public MonthHappinessAndDateDto(Feed feed){
        this.happiness = feed.getHappiness();
        this.formattedDate = feed.getDate().toString().substring(4,6) + "/" + feed.getDate().toString().substring(6,8);
    }
}
