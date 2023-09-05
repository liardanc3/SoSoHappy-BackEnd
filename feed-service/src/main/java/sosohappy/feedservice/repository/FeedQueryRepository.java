package sosohappy.feedservice.repository;

import sosohappy.feedservice.domain.dto.FeedDto;
import sosohappy.feedservice.domain.dto.HappinessDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedDtoBySearchFeedFilter(NicknameAndDateDto nicknameAndDateDto);

    Optional<FeedDto> findDayFeedDtoBySearchFeedFilter(NicknameAndDateDto nicknameAndDateDto);

    Optional<Feed> findByNicknameAndDate(String nickname, Long date);

}
