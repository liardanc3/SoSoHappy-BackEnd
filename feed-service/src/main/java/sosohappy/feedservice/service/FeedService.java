package sosohappy.feedservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;
import sosohappy.feedservice.domain.dto.FeedDto;
import sosohappy.feedservice.domain.dto.SearchFeedFilter;
import sosohappy.feedservice.domain.dto.UpdateResultDto;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.exception.custom.FindException;
import sosohappy.feedservice.repository.FeedRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedService {

    private final FeedRepository feedRepository;

    public List<FeedDto> findMonthFeed(SearchFeedFilter searchFeedFilter) {
        return Optional.ofNullable(feedRepository.findMonthFeedBySearchFeedFilter(searchFeedFilter))
                .filter(list -> !list.isEmpty())
                .orElseThrow(FindException::new);
    }
    public FeedDto findDayFeed(SearchFeedFilter searchFeedFilter) {
        return feedRepository.findDayFeedBySearchFeedFilter(searchFeedFilter)
                .orElseThrow(FindException::new);
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
}
