package sosohappy.diaryservice.repository;

import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter);

    FeedDto findDayFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter);
}
