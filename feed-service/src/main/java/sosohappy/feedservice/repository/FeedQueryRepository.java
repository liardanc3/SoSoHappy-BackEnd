package sosohappy.feedservice.repository;

import sosohappy.feedservice.domain.dto.FeedDto;
import sosohappy.feedservice.domain.dto.SearchFeedFilter;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedDtoBySearchFeedFilter(SearchFeedFilter searchFeedFilter);

    Optional<FeedDto> findDayFeedDtoBySearchFeedFilter(SearchFeedFilter searchFeedFilter);

    Optional<Feed> findByNicknameAndDate(String nickname, Long date);
}
