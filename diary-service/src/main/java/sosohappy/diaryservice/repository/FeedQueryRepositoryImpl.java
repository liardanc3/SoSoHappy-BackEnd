package sosohappy.diaryservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sosohappy.diaryservice.domain.dto.FeedDto;
import sosohappy.diaryservice.domain.dto.SearchFeedFilter;
import sosohappy.diaryservice.domain.entity.QFeed;

import java.util.List;

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
                        feed.nickname.eq(searchFeedFilter.getNickname())
                )
                .orderBy(feed.date.asc())
                .fetch();
    }
}
