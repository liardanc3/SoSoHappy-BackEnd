package sosohappy.feedservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.domain.entity.*;
import sosohappy.feedservice.exception.custom.ValidException;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static sosohappy.feedservice.domain.entity.QFeed.feed;
import static sosohappy.feedservice.domain.entity.QFeedImage.feedImage;
import static sosohappy.feedservice.domain.entity.QFeedCategory.feedCategory;
import static sosohappy.feedservice.domain.entity.QFeedLikeNickname.feedLikeNickname;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserFeedDto> findMonthFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        monthEq(nicknameAndDateDto.getDate()),
                        nickNameEq(nicknameAndDateDto.getNickname())
                )
                .orderBy(feed.date.desc())
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        UserFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname))
                                )
                        )
                );
    }

    @Override
    public UserFeedDto findDayFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        dayEq(nicknameAndDateDto.getDate()),
                        nickNameEq(nicknameAndDateDto.getNickname())
                )
                .orderBy(feed.date.desc())
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        UserFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname))
                                )
                        )
                )
                .stream().findFirst()
                .orElse(null);
    }

    @Override
    public Optional<Feed> findByNicknameAndDate(String nickname, Long date) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(feed)
                        .where(
                                nickNameEq(nickname),
                                dayEq(date)
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<HappinessAndCategoryDto> findMonthHappinessAndCategoryDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .select(Projections.constructor(
                        HappinessAndCategoryDto.class,
                        feed
                ))
                .from(feed)
                .where(
                        nickNameEq(nicknameAndDateDto.getNickname()),
                        monthEq(nicknameAndDateDto.getDate())
                )
                .fetch();
    }

    @Override
    public List<HappinessAndDateDto> findHappinessAndDateDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .select(Projections.constructor(
                        HappinessAndDateDto.class,
                        feed
                ))
                .from(feed)
                .where(
                        nickNameEq(nicknameAndDateDto.getNickname()),
                        monthEq(nicknameAndDateDto.getDate())
                )
                .orderBy(feed.date.asc())
                .fetch();
    }

    @Override
    public Optional<Double> findMonthHappinessAvgByNicknameAndDate(String nickname, Long date) {
        return Optional.ofNullable(
                queryFactory
                        .select(feed.happiness.avg())
                        .from(feed)
                        .where(
                                nickNameEq(nickname),
                                monthEq(date)
                        )
                        .fetchOne()
        );
    }

    @Override
    public Slice<OtherFeedDto> findByNicknameAndDateWithSlicing(String nickname, Long date, Pageable pageable) {
        if(pageable.getPageSize() > 99){
            throw new ValidException();
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        List<OtherFeedDto> otherFeedDtos = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        isDayFind(date),
                        nickNameEq(nickname).not()
                )
                .orderBy(feed.date.desc())
                .offset(0)
                .limit(6L * page * (size + 1) + size * 6L + 1)
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        OtherFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname)),
                                        Expressions.asString(nickname)
                                )
                        )
                );

        List<OtherFeedDto> feedList = otherFeedDtos.subList(
                Math.min(otherFeedDtos.size(), page * size),
                Math.min(otherFeedDtos.size(), (page * size) + size + 1)
        );

        boolean hasNext = false;
        if (feedList.size() > pageable.getPageSize()){
            feedList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(feedList, pageable, hasNext);
    }

    @Override
    public Slice<OtherFeedDto> findUserFeed(String srcNickname, String dstNickname, Pageable pageable) {
        if(pageable.getPageSize() > 25){
            throw new ValidException();
        }

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        List<OtherFeedDto> otherFeedDtos = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        nickNameEq(dstNickname)
                )
                .orderBy(feed.date.desc())
                .offset(0)
                .limit(6L * page * (size + 1) + size * 6L + 1)
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        OtherFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname)),
                                        Expressions.asString(srcNickname)
                                )
                        )
                );

        List<OtherFeedDto> feedList = otherFeedDtos.subList(
                Math.min(otherFeedDtos.size(), page * size),
                Math.min(otherFeedDtos.size(), (page * size) + size + 1)
        );

        boolean hasNext = false;
        if (feedList.size() > pageable.getPageSize()){
            feedList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(feedList, pageable, hasNext);
    }

    @Override
    public Optional<OtherFeedDto> findBySrcNicknameAndDstNicknameAndDate(String srcNickname, String dstNickname, Long date) {
        return queryFactory
                .selectFrom(feed)
                .leftJoin(feed.feedImages, feedImage)
                .leftJoin(feed.feedCategories, feedCategory)
                .leftJoin(feed.feedLikeNicknames, feedLikeNickname)
                .where(
                        dayEq(date),
                        nickNameEq(dstNickname)
                )
                .orderBy(feed.date.desc())
                .transform(
                        groupBy(feed.id).list(
                                Projections.constructor(
                                        OtherFeedDto.class,
                                        feed,
                                        list(Projections.constructor(Long.class, feedImage.id)),
                                        list(Projections.constructor(FeedCategory.class, feed, feedCategory.category)),
                                        list(Projections.constructor(FeedLikeNickname.class, feed, feedLikeNickname.nickname)),
                                        Expressions.asString(srcNickname)
                                )
                        )
                )
                .stream().findAny();
    }

    // ----------------------------------------------------------------- //

    private BooleanExpression nickNameEq(String nickname){
        return feed.nickname.eq(nickname);
    }

    private BooleanExpression monthEq(Long date) {
        return feed.date.divide(10000000000L).floor().eq(date / 10000000000L);
    }

    private BooleanExpression dayEq(Long date){
        return feed.date.divide(100000000L).floor().eq(date / 100000000L);
    }

    private BooleanExpression isDayFind(Long date){
        if(date != -1){
            return dayEq(date);
        }
        return null;
    }
}
