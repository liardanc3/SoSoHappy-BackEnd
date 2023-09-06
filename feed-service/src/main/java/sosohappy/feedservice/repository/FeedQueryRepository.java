package sosohappy.feedservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.domain.entity.Feed;

import java.util.List;
import java.util.Optional;

public interface FeedQueryRepository {

    List<UserFeedDto> findMonthFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    Optional<UserFeedDto> findDayFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    Optional<Feed> findByNicknameAndDate(String nickname, Long date);

    List<HappinessAndCategoryDto> findMonthHappinessAndCategoryDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    List<HappinessAndDateDto> findHappinessAndDateDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto);

    Optional<Double> findMonthHappinessAvgByNicknameAndDate(String nickname, Long date);

    Slice<OtherFeedDto> findByNicknameAndDateWithSlicing(String nickname, Long date, Pageable pageable);

    Slice<OtherFeedDto> findUserFeed(String srcNickname, String dstNickname, Pageable pageable);
}
