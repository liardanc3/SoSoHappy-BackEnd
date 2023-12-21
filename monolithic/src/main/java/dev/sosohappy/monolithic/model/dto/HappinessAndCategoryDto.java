package dev.sosohappy.monolithic.model.dto;

import lombok.Data;

import dev.sosohappy.monolithic.model.entity.*;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class HappinessAndCategoryDto {

    private Integer happiness;
    private List<String> categories;

    public HappinessAndCategoryDto(Feed feed) {
        this.happiness = feed.getHappiness();
        this.categories = feed.getFeedCategories().stream().map(FeedCategory::getCategory).collect(Collectors.toList());
    }
}
