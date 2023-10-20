package sosohappy.feedservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import sosohappy.feedservice.domain.dto.*;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.exception.custom.ValidException;

import java.util.List;
import java.util.Optional;

import static sosohappy.feedservice.domain.entity.QFeed.*;

@Repository
@RequiredArgsConstructor
public class FeedQueryRepositoryImpl implements FeedQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserFeedDto> findMonthFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return queryFactory
                .select(Projections.constructor(
                        UserFeedDto.class,
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
    public Optional<UserFeedDto> findDayFeedDtoByNicknameAndDateDto(NicknameAndDateDto nicknameAndDateDto) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(
                                UserFeedDto.class,
                                feed
                        ))
                        .from(feed)
                        .where(
                                nickNameEq(nicknameAndDateDto.getNickname()),
                                dayEq(nicknameAndDateDto.getDate())
                        )
                        .fetchOne()
        );
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
        if(pageable.getPageSize() > 25){
            throw new ValidException();
        }

        List<OtherFeedDto> feedList = queryFactory
                .select(Projections.constructor(
                        OtherFeedDto.class,
                        feed, Expressions.asString(nickname)
                ))
                .from(feed)
                .where(
                        isDayFind(date),
                        nickNameEq(nickname).not()
                )
                .orderBy(feed.date.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

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

        List<OtherFeedDto> feedList = queryFactory
                .select(Projections.constructor(
                        OtherFeedDto.class,
                        feed, Expressions.asString(srcNickname)
                ))
                .from(feed)
                .where(
                        nickNameEq(dstNickname)
                )
                .orderBy(feed.date.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (feedList.size() > pageable.getPageSize()){
            feedList.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(feedList, pageable, hasNext);
    }

    @Override
    public Optional<OtherFeedDto> findBySrcNicknameAndDstNicknameAndDate(String srcNickname, String dstNickname, Long date) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.constructor(
                                OtherFeedDto.class,
                                feed, Expressions.asString(srcNickname)
                        ))
                        .from(feed)
                        .where(
                                nickNameEq(dstNickname),
                                dayEq(date)
                        )
                        .fetchOne()
        );
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
