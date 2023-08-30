package sosohappy.diaryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;
import sosohappy.diaryservice.repository.FeedRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    public List<FeedDto> findMonthFeed(SearchFeedFilter searchFeedFilter) {
        return feedRepository.findMonthFeedBySearchFeedFilter(searchFeedFilter);
    }

    public FeedDto findDayFeed(SearchFeedFilter searchFeedFilter) {
        return feedRepository.findDayFeedBySearchFeedFilter(searchFeedFilter);
    }
}
