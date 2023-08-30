package sosohappy.diaryservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;

import java.util.List;
import java.util.Optional;

import static sosohappy.diaryservice.domain.entity.QFeed.*;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<FeedDto> findMonthFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter) {
        return queryFactory
                .select(Projections.constructor(
                        FeedDto.class,
                        feed
                ))
                .from(feed)
                .where(
                        nickNameEq(searchFeedFilter.getNickname()),
                        monthEq(searchFeedFilter.getDate())
                )
                .orderBy(feed.date.asc())
                .fetch();
    }

    @Override
    public FeedDto findDayFeedBySearchFeedFilter(SearchFeedFilter searchFeedFilter) {
        return queryFactory
                .select(Projections.constructor(
                        FeedDto.class,
                        feed
                ))
                .from(feed)
                .where(
                        nickNameEq(searchFeedFilter.getNickname()),
                        dayEq(searchFeedFilter.getDate())
                )
                .fetchOne();
    }

    // ----------------------------------------------------------------- //

    private BooleanExpression nickNameEq(String nickname){
        return feed.nickname.eq(nickname);
    }

    private BooleanExpression monthEq(Long date) {
        return feed.date.mod(10000000000L).eq(date % 10000000000L);
    }

    private BooleanExpression dayEq(Long date){
        return feed.date.mod(100000000L).eq(date % 100000000L);
    }
}
