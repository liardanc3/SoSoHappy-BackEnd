package sosohappy.diaryservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;
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
}