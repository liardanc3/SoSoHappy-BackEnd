package sosohappy.feedservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;
import sosohappy.feedservice.domain.dto.FeedDto;
import sosohappy.feedservice.domain.dto.SearchFeedFilter;
import sosohappy.feedservice.domain.dto.UpdateResultDto;
import sosohappy.feedservice.service.FeedService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/findMonthFeed")
    public List<FeedDto> findMonthFeed(@ModelAttribute SearchFeedFilter searchFeedFilter){
        return feedService.findMonthFeed(searchFeedFilter);
    }

    @PostMapping("/findDayFeed")
    public FeedDto findDayFeed(@ModelAttribute SearchFeedFilter searchFeedFilter){
        return feedService.findDayFeed(searchFeedFilter);
    }

    @PostMapping("/saveFeed")
    public UpdateResultDto saveFeed(@ModelAttribute UpdateFeedDto updateFeedDto){
        return feedService.updateFeed(updateFeedDto);
    }
}
