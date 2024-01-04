package dev.sosohappy.monolithic.service;

import dev.sosohappy.monolithic.model.dto.*;
import dev.sosohappy.monolithic.model.entity.Feed;
import dev.sosohappy.monolithic.repository.rdbms.FeedImageRepository;
import dev.sosohappy.monolithic.repository.rdbms.FeedLikeNicknameRepository;
import dev.sosohappy.monolithic.repository.rdbms.FeedRepository;
import dev.sosohappy.monolithic.exception.custom.*;
import dev.sosohappy.monolithic.repository.rdbms.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final FeedImageRepository feedImageRepository;

    private final NoticeService noticeService;
    private final FeedLikeNicknameRepository feedLikeNicknameRepository;
    private final HappinessService happinessService;

    public List<UserFeedDto> findMonthFeed(NicknameAndDateDto nicknameAndDateDto) {
        return Optional.ofNullable(feedRepository.findMonthFeedDtoByNicknameAndDateDto(nicknameAndDateDto))
                .orElse(List.of());
    }

    public UserFeedDto findDayFeed(NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findDayFeedDtoByNicknameAndDateDto(nicknameAndDateDto);
    }

    public UpdateResultDto updateFeed(UpdateFeedDto updateFeedDto) {
        return feedRepository.findByNicknameAndDate(updateFeedDto.getNickname(), updateFeedDto.getDate())
                .map(feed -> {
                    feed.updateFeed(updateFeedDto);
                    return UpdateResultDto.updateSuccess("등록 성공");
                })
                .orElseGet(() -> {
                    feedRepository.save(new Feed(updateFeedDto));
                    return UpdateResultDto.updateSuccess("등록 성공");
                });
    }

    public UpdateResultDto updatePublicStatus(NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findByNicknameAndDate(nicknameAndDateDto.getNickname(), nicknameAndDateDto.getDate())
                .map(feed -> {
                    feed.updateIsPublic();
                    return UpdateResultDto.updateSuccess("업데이트 성공");
                })
                .orElseThrow(NotFoundException::new);
    }

    public SliceResponse<OtherFeedDto> findOtherFeed(String nickname, Long date, Pageable pageable) {
        return new SliceResponse<>(feedRepository.findByNicknameAndDateWithSlicing(nickname, date, pageable));
    }

    public SliceResponse<OtherFeedDto> findUserFeed(String srcNickname, String dstNickname, Pageable pageable) {
        return new SliceResponse<>(feedRepository.findUserFeed(srcNickname, dstNickname, pageable));
    }

    public OtherFeedDto findDetailFeed(String srcNickname, String dstNickname, Long date) {
        return feedRepository.findBySrcNicknameAndDstNicknameAndDate(srcNickname, dstNickname, date)
                .orElse(null);
    }

    public Map<String, Boolean> updateLike(String srcNickname, NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findByNicknameAndDate(nicknameAndDateDto.getNickname(), nicknameAndDateDto.getDate())
                .map(feed ->  {
                    Map<String, Boolean> responseDto = Map.of("like", updateLike(feed, srcNickname));

                    if(responseDto.get("like")){
                        noticeService.sendNotice(
                                srcNickname,
                                nicknameAndDateDto.getNickname(),
                                nicknameAndDateDto.getDate(),
                                userRepository.findDeviceTokenByNickname(nicknameAndDateDto.getNickname())
                        );
                    }

                    return responseDto;
                })
                .orElseThrow(NotFoundException::new);
    }

    public void updateNickname(String srcNickname, String dstNickname){
        feedRepository.updateFeedNickname(srcNickname, dstNickname);
    }

    public void deleteDataOfResignedUser(String nickname){
        feedRepository.deleteByNickname(nickname);
    }

    public byte[] findImage(String imageId) {
        try{

            ImageDto image = feedImageRepository.findImageById(Long.parseLong(imageId));

            if(image == null){
                throw new NotFoundException();
            }

            return image.getImage();

        } catch (NumberFormatException e) {
            throw new ValidException();
        }
    }

    public Map<String, Boolean> deleteFeed(NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findByNicknameAndDate(nicknameAndDateDto.getNickname(), nicknameAndDateDto.getDate())
                    .map(feed -> {
                        feedRepository.delete(feed);
                        return Map.of("result", true);
                    })
                    .orElseThrow(NotFoundException::new);
    }

    // --------------------------------------------------------------------------------------------------- //


    private boolean updateLike(Feed feed, String srcNickname){
        return feedLikeNicknameRepository.findByFeedAndNickname(feed, srcNickname)
                .map(feedLikeNickname -> {
                    feed.getFeedLikeNicknames().remove(feedLikeNickname);
                    return false;
                })
                .orElseGet(() -> {
                    feed.like(srcNickname);
                    return true;
                });
    }

}
