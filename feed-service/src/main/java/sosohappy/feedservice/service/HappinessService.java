package sosohappy.feedservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.feedservice.domain.dto.AnalysisDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.repository.FeedRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class HappinessService {

    private final ConcurrentHashMap<String, Integer> categoryToIndexMap;
    private final ConcurrentHashMap<Integer, String> indexToCategoryMap;
    private final AtomicReference<List<List<Integer>>> atomicSimilarityMatrix;
    private final FeedRepository feedRepository;

    @PostConstruct
    void updateSimilarityMatrix() {
        atomicSimilarityMatrix.set(initMatrix(allocateMatrix()));
    }

    public AnalysisDto analysisHappiness(NicknameAndDateDto nicknameAndDateDto) {

        List<String> bestCategoryList = getBestCategoryList(nicknameAndDateDto);
        List<String> recommendCategoryList = getRecommendCategoryList(bestCategoryList);

        return new AnalysisDto(bestCategoryList, recommendCategoryList);
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
        indexAndPointList.sort(Map.Entry.comparingByValue());

        return indexAndPointList
                .subList(0, Math.min(10, indexAndPointList.size()))
                .stream()
                .map(Map.Entry::getKey)
                .map(indexToCategoryMap::get)
                .toList();
    }

    private List<String> getBestCategoryList(NicknameAndDateDto nicknameAndDateDto) {
        Map<String, Integer> categoryAndPointMap = new HashMap<>();

        feedRepository.findMonthHappinessDtoByNicknameAndDateDto(nicknameAndDateDto)
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

    private List<List<Integer>> initMatrix(List<List<Integer>> matrix) {
        feedRepository.findHappinessDtoAll()
                .forEach(happinessDto -> {
                    List<String> categories = happinessDto.getCategories();

                    for (int i = 0; i < categories.size(); i++){

                        int srcIdx = categoryToIndexMap.get(categories.get(i));

                        for (int j = 0; j < categories.size(); j++){
                            if (i != j){

                                int dstIdx = categoryToIndexMap.get(categories.get(j));

                                matrix.get(srcIdx)
                                        .set(
                                                dstIdx,
                                                matrix.get(srcIdx).get(dstIdx) + happinessDto.getHappiness()
                                        );
                            }
                        }
                    }
                });

        return matrix;
    }

    private List<List<Integer>> allocateMatrix() {
        int size = (int) feedRepository.findAllCategories()
                .stream()
                .map(category -> {
                    categoryToIndexMap.putIfAbsent(category, categoryToIndexMap.size());
                    indexToCategoryMap.putIfAbsent(categoryToIndexMap.size()-1, category);
                    return 1;
                })
                .count();

        List<List<Integer>> matrix = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            matrix.add(new ArrayList<>(Collections.nCopies(size, 0)));
        }

        return matrix;
    }
}
