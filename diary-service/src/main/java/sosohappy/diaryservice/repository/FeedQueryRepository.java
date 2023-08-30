package sosohappy.diaryservice.repository;

import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;

import java.util.List;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter);
}
