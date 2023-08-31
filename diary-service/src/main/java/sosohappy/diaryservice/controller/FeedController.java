package sosohappy.diaryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.diaryservice.domain.dto.UpdateFeedDto;
import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;
import sosohappy.diaryservice.domain.dto.UpdateResultDto;
import sosohappy.diaryservice.service.FeedService;

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
