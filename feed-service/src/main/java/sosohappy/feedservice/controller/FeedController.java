package sosohappy.feedservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.service.FeedService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @PostMapping("/findMonthFeed")
    public List<UserFeedDto> findMonthFeed(@ModelAttribute NicknameAndDateDto nicknameAndDateDto){
        return feedService.findMonthFeed(nicknameAndDateDto);
    }

    @PostMapping("/findDayFeed")
    public UserFeedDto findDayFeed(@ModelAttribute NicknameAndDateDto nicknameAndDateDto){
        return feedService.findDayFeed(nicknameAndDateDto);
    }

    @PostMapping("/saveFeed")
    public UpdateResultDto saveFeed(@ModelAttribute UpdateFeedDto updateFeedDto){
        return feedService.updateFeed(updateFeedDto);
    }

    @PostMapping("/updatePublicStatus")
    public UpdateResultDto updatePublicStatus(@ModelAttribute NicknameAndDateDto nicknameAndDateDto){
        return feedService.updatePublicStatus(nicknameAndDateDto);
    }

    @GetMapping("/findOtherFeed")
    public SliceResponse<OtherFeedDto> findOtherDayFeed(@RequestParam String nickname,
                                                        @RequestParam @Nullable Long date,
                                                        @PageableDefault(size = 7) Pageable pageable){
        return feedService.findOtherFeed(nickname, date == null ? -1 : date, pageable);
    }

}
