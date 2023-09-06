package sosohappy.feedservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.feedservice.domain.dto.AnalysisDto;
import sosohappy.feedservice.domain.dto.MonthHappinessAndDateDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.domain.dto.UpdateFeedDto;
import sosohappy.feedservice.domain.entity.Feed;
import sosohappy.feedservice.exception.custom.FindException;
import sosohappy.feedservice.repository.FeedRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HappinessService {

    private final ConcurrentHashMap<String, Integer> categoryToIndexMap;
    private final ConcurrentHashMap<Integer, String> indexToCategoryMap;
    private final AtomicReference<List<List<Integer>>> atomicSimilarityMatrix;
    private final FeedRepository feedRepository;

    @PostConstruct
    void initSimilarityMatrix() {
        allocateMatrix();
        initMatrix();
    }

    public AnalysisDto analysisHappiness(NicknameAndDateDto nicknameAndDateDto) {
        List<String> bestCategoryList = getBestCategoryList(nicknameAndDateDto);
        List<String> recommendCategoryList = getRecommendCategoryList(bestCategoryList);

        return new AnalysisDto(bestCategoryList, recommendCategoryList);
    }

    public void updateSimilarityMatrix(Feed feed, UpdateFeedDto updateFeedDto) {
        Integer srcHappiness = feed.getHappiness();
        List<String> srcCategoryList = feed.getCategoryList();
        
        updateSimilarity(-srcHappiness, srcCategoryList);

        Integer dstHappiness = updateFeedDto.getHappiness();
        List<String> dstCategoryList = updateFeedDto.getCategoryList();

        updateSimilarity(dstHappiness, dstCategoryList);
    }

    public void updateSimilarityMatrix(UpdateFeedDto updateFeedDto) {
        Integer happiness = updateFeedDto.getHappiness();
        List<String> categoryList = updateFeedDto.getCategoryList();
        
        updateSimilarity(happiness, categoryList);
    }

    public List<MonthHappinessAndDateDto> findMonthHappiness(NicknameAndDateDto nicknameAndDateDto) {
        return Optional.ofNullable(feedRepository.findMonthHappinessAndDateDtoByNicknameAndDateDto(nicknameAndDateDto))
                .filter(monthHappinessAndDateDtoList -> !monthHappinessAndDateDtoList.isEmpty())
                .orElseThrow(FindException::new);

    }

    // ------------------------------------------------------------------------------- //

    private List<String> getRecommendCategoryList(List<String> bestCategoryList) {
        Map<Integer, Integer> indexAndPointMap = new HashMap<>();
        bestCategoryList
                .forEach(category -> {
                    int idx = categoryToIndexMap.get(category);

                    List<List<Integer>> similarityMatrix = atomicSimilarityMatrix.get();

                    for (int i = 0; i < similarityMatrix.get(idx).size(); i++){
                        int point = similarityMatrix.get(idx).get(i);

                        indexAndPointMap.compute(i, (k, v) -> (v == null) ? point : v + point);

                    }
                });
        List<Map.Entry<Integer, Integer>> indexAndPointList = new ArrayList<>(indexAndPointMap.entrySet());
        indexAndPointList.sort(Map.Entry.<Integer, Integer>comparingByValue().reversed());

        return indexAndPointList
                .subList(0, Math.min(10, indexAndPointList.size()))
                .stream()
                .map(Map.Entry::getKey)
                .map(indexToCategoryMap::get)
                .filter(category -> !bestCategoryList.contains(category))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> getBestCategoryList(NicknameAndDateDto nicknameAndDateDto) {
        Map<String, Integer> categoryAndPointMap = new HashMap<>();

        feedRepository.findMonthHappinessAndCategoryDtoByNicknameAndDateDto(nicknameAndDateDto)
                .forEach(happinessDto -> {
                    Integer point = happinessDto.getHappiness();

                    happinessDto.getCategories()
                            .forEach(category ->
                                    categoryAndPointMap.compute(category, (k, v) -> (v == null) ? point : v + point)
                            );
                });

        List<Map.Entry<String, Integer>> categoryAndPointList = new ArrayList<>(categoryAndPointMap.entrySet());
        categoryAndPointList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return categoryAndPointList
                .subList(0, Math.min(3, categoryAndPointList.size()))
                .stream()
                .map(Map.Entry::getKey)
                .toList();
    }

    private void initMatrix() {
        feedRepository.findHappinessAndCategoryDtoAll()
                .forEach(happinessDto -> {
                    Integer happiness = happinessDto.getHappiness();
                    List<String> categories = happinessDto.getCategories();

                    updateSimilarity(happiness, categories);
                });
    }

    private void updateSimilarity(Integer happiness, List<String> categories) {
        List<List<Integer>> matrix = atomicSimilarityMatrix.get();

        for (String category : categories) {
            updateCategory(category);
        }

        for (int i = 0; i < categories.size(); i++){

            int srcIdx = categoryToIndexMap.get(categories.get(i));

            for (int j = 0; j < categories.size(); j++){
                if (i != j){

                    int dstIdx = categoryToIndexMap.get(categories.get(j));

                    matrix.get(srcIdx)
                            .set(
                                    dstIdx,
                                    matrix.get(srcIdx).get(dstIdx) + happiness
                            );
                }
            }
        }
    }

    private void allocateMatrix() {
        feedRepository.findAllCategories()
                .forEach(this::updateCategory);

        int size = Math.max(categoryToIndexMap.size(), 100);
        atomicSimilarityMatrix.set(
                Stream.generate(() ->
                                Stream.generate(() -> 0)
                                        .limit(size)
                                        .collect(Collectors.toList())
                        )
                        .limit(size)
                        .collect(Collectors.toList())
        );
    }

    private void updateCategory(String category) {
        categoryToIndexMap.putIfAbsent(category, categoryToIndexMap.size());
        indexToCategoryMap.putIfAbsent(categoryToIndexMap.size()-1, category);
    }

}
