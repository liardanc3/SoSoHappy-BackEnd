package sosohappy.feedservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.service.FeedService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/findMonthFeed")
    public List<UserFeedDto> findMonthFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findMonthFeed(nicknameAndDateDto);
    }

    @PostMapping("/findDayFeed")
    public UserFeedDto findDayFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findDayFeed(nicknameAndDateDto);
    }

    @PostMapping("/saveFeed")
    public UpdateResultDto saveFeed(@ModelAttribute @Valid UpdateFeedDto updateFeedDto){
        return feedService.updateFeed(updateFeedDto);
    }

    @PostMapping("/updatePublicStatus")
    public UpdateResultDto updatePublicStatus(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.updatePublicStatus(nicknameAndDateDto);
    }

    @GetMapping("/findOtherFeed")
    public SliceResponse<OtherFeedDto> findOtherDayFeed(@RequestParam @Valid @NotEmpty String nickname,
                                                        @RequestParam Long date,
                                                        @PageableDefault(size = 7) Pageable pageable){
        return feedService.findOtherFeed(nickname, date == null ? -1 : date, pageable);
    }

    @PostMapping("/updateLike")
    public Map<String, Boolean> updateLike(@Valid @Size(min = 1, max = 10) String srcNickname, @ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.updateLike(srcNickname, nicknameAndDateDto);
    }

    @GetMapping("/findUserFeed")
    public SliceResponse<OtherFeedDto> findUserFeed(@Valid @Size(min = 1, max = 10) @RequestParam String srcNickname,
                                                    @Valid @Size(min = 1, max = 10) @RequestParam String dstNickname,
                                                    @PageableDefault(size = 7) Pageable pageable){
        return feedService.findUserFeed(srcNickname, dstNickname, pageable);
    }

    @PostMapping("/findDetailFeed")
    public OtherFeedDto findDetailFeed(@Valid @Size(min = 1, max = 10) @RequestParam String srcNickname,
                                       @Valid @Size(min = 1, max = 10) @RequestParam String dstNickname,
                                       @Valid @Min(value = 2000000000000000L) @Max(value = 9999999999999999L) @RequestParam Long date){
        return feedService.findDetailFeed(srcNickname, dstNickname, date);
    }

}
