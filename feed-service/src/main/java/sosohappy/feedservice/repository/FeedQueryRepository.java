package sosohappy.feedservice.repository;

import sosohappy.feedservice.domain.dto.FeedDto;
import sosohappy.feedservice.domain.dto.HappinessAndCategoryDto;
import sosohappy.feedservice.domain.dto.DayHappinessAndDateDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<FeedDto> findMonthFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    Optional<FeedDto> findDayFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    Optional<Feed> findByNicknameAndDate(String nickname, Long date);

    List<HappinessAndCategoryDto> findMonthHappinessAndCategoryDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    List<DayHappinessAndDateDto> findDayHappinessAndDateDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

}
