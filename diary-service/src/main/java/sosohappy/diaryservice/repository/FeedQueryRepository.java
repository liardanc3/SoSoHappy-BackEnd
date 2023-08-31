package sosohappy.diaryservice.repository;

import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;
import sosohappy.diaryservice.domain.entity.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter);

    FeedDto findDayFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter);

    Optional<Feed> findByNicknameAndDate(String nickname, Long date);
}
