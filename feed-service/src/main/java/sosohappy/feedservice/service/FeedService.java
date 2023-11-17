package sosohappy.feedservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.integration.annotation.Default;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.exception.custom.FindException;
import sosohappy.feedservice.exception.custom.UpdateException;
import sosohappy.feedservice.kafka.KafkaProducer;
import sosohappy.feedservice.repository.FeedRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedService {

    private final FeedRepository feedRepository;
    private final HappinessService happinessService;
    private final ObjectProvider<FeedService> feedServiceObjectProvider;

    public List<UserFeedDto> findMonthFeed(NicknameAndDateDto nicknameAndDateDto) {
        return Optional.ofNullable(feedRepository.findMonthFeedDtoByNicknameAndDateDto(nicknameAndDateDto))
                .orElse(List.of());
    }
    public UserFeedDto findDayFeed(NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findDayFeedDtoByNicknameAndDateDto(nicknameAndDateDto)
                .orElse(null);
    }

    public UpdateResultDto updateFeed(UpdateFeedDto updateFeedDto, HttpServletRequest request) {
        return feedRepository.findByNicknameAndDate(updateFeedDto.getNickname(), updateFeedDto.getDate())
                .map(feed -> {
                    happinessService.updateSimilarityMatrix(feed, updateFeedDto);
                    feed.updateFeed(updateFeedDto);
                    return UpdateResultDto.updateSuccess("등록 성공");
                })
                .orElseGet(() -> {
                    happinessService.updateSimilarityMatrix(updateFeedDto);
                    feedRepository.save(new Feed(updateFeedDto));
                    return UpdateResultDto.updateSuccess("등록 성공");
                });
    }

    public UpdateResultDto updatePublicStatus(NicknameAndDateDto nicknameAndDateDto) {
        return feedRepository.findByNicknameAndDate(nicknameAndDateDto.getNickname(), nicknameAndDateDto.getDate())
                .map(Feed::updateIsPublic)
                .map(feed -> UpdateResultDto.updateSuccess("업데이트 성공"))
                .orElseThrow(UpdateException::new);
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
                    Map<String, Boolean> responseDto = Map.of("like", feed.updateLike(srcNickname));
                    if(responseDto.get("like")){
                        feedServiceObjectProvider.getObject().produceUpdateLike(srcNickname, nicknameAndDateDto);
                    }
                    return responseDto;
                })
                .orElseGet(() -> Map.of("like", false));
    }

    public void updateNickname(String srcNickname, String dstNickname){
        feedRepository.updateFeedNickname(srcNickname, dstNickname);
    }

    public void deleteDataOfResignedUser(String nickname){
        feedRepository.deleteByNickname(nickname);

        feedRepository.findByLikeNicknameSetContaining(nickname)
                .forEach(feed -> feed.getLikeNicknameSet().remove(nickname));
    }

    // --------------------------------------------------------------------------------------------------- //

    @KafkaProducer(topic = "noticeLike")
    public List<String> produceUpdateLike(String srcNickname, NicknameAndDateDto nicknameAndDateDto) {
        return List.of(srcNickname, nicknameAndDateDto.getNickname() + "," + nicknameAndDateDto.getDate());
    }

}
