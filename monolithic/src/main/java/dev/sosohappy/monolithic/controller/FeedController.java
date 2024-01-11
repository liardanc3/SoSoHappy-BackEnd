package dev.sosohappy.monolithic.controller;

import dev.sosohappy.monolithic.repository.rdbms.FeedImageRepository;
import dev.sosohappy.monolithic.service.*;
import dev.sosohappy.monolithic.model.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feed-service")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;
    private final FeedImageRepository feedImageRepository;

    @PostMapping("/findMonthFeed")
    public List<UserFeedDto> findMonthFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findMonthFeed(nicknameAndDateDto);
    }

    @PostMapping("/findDayFeed")
    public UserFeedDto findDayFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.findDayFeed(nicknameAndDateDto);
    }

    @PostMapping("/saveFeed")
    public synchronized UpdateResultDto saveFeed(@ModelAttribute @Valid UpdateFeedDto updateFeedDto){
        return feedService.updateFeed(updateFeedDto);
    }

    @PostMapping("/updatePublicStatus")
    public UpdateResultDto updatePublicStatus(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.updatePublicStatus(nicknameAndDateDto);
    }

    @GetMapping("/findOtherFeed")
    public SliceResponse<OtherFeedDto> findOtherDayFeed(@RequestParam String nickname,
                                                        @RequestParam @Nullable Long date,
                                                        @PageableDefault(size = 7) Pageable pageable){
        return feedService.findOtherFeed(nickname, date == null ? -1 : date, pageable);
    }

    @PostMapping("/updateLike")
    public Map<String, Boolean> updateLike(@RequestParam String srcNickname, @ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.updateLike(srcNickname, nicknameAndDateDto);
    }

    @GetMapping("/findUserFeed")
    public SliceResponse<OtherFeedDto> findUserFeed(@RequestParam String srcNickname,
                                                    @RequestParam String dstNickname,
                                                    @PageableDefault(size = 7) Pageable pageable){
        return feedService.findUserFeed(srcNickname, dstNickname, pageable);
    }

    @PostMapping("/findDetailFeed")
    public OtherFeedDto findDetailFeed(@RequestParam String srcNickname,
                                       @RequestParam String dstNickname,
                                       @RequestParam Long date){
        return feedService.findDetailFeed(srcNickname, dstNickname, date);
    }

    @PostMapping("/deleteFeed")
    public Map<String, Boolean> deleteFeed(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return feedService.deleteFeed(nicknameAndDateDto);
    }

    @PostMapping("/findFeedImage")
    public ImageDto findFeedImage(@ModelAttribute @Valid ImageIdDto imageIdDto){
        return feedImageRepository.findImageById(imageIdDto.getImageId());
    }

    @GetMapping(value = "/image/{imageId}", produces = "image/heic")
    public byte[] findImage(@PathVariable String imageId){
        return feedService.findImage(imageId);
    }

}
