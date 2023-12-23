package dev.sosohappy.monolithic.service;

import dev.sosohappy.monolithic.model.dto.AnalysisDto;
import dev.sosohappy.monolithic.model.dto.HappinessAndDateDto;
import dev.sosohappy.monolithic.model.dto.NicknameAndDateDto;
import dev.sosohappy.monolithic.model.dto.UpdateFeedDto;
import dev.sosohappy.monolithic.model.entity.Feed;
import dev.sosohappy.monolithic.model.entity.FeedCategory;
import dev.sosohappy.monolithic.repository.rdbms.FeedCategoryRepository;
import dev.sosohappy.monolithic.repository.rdbms.FeedRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HappinessService {

    private static final ConcurrentHashMap<String, Integer> categoryToIndexMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, String> indexToCategoryMap = new ConcurrentHashMap<>();
    private static final AtomicReference<List<List<Integer>>> atomicSimilarityMatrix = new AtomicReference<>();
    private static final Map<String, List<String>> categoryAndSentenceMap = new HashMap<>();

    private final FeedRepository feedRepository;
    private final FeedCategoryRepository feedCategoryRepository;

    @PostConstruct
    void initSimilarityMatrix() {
        allocateMatrix();
        initMatrix();
        initCategoryAndSentenceMap();
    }

    public AnalysisDto analysisHappiness(NicknameAndDateDto nicknameAndDateDto) {
        List<String> bestCategoryList = getBestCategoryList(nicknameAndDateDto);

        return new AnalysisDto(bestCategoryList, convertCategoryToSentence(bestCategoryList));
    }

    public void updateSimilarityMatrix(Feed feed, UpdateFeedDto updateFeedDto) {
        Integer srcHappiness = feed.getHappiness();
        List<String> srcCategoryList = feed.getFeedCategories().stream().map(FeedCategory::getCategory).collect(Collectors.toList());
        
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

    public List<HappinessAndDateDto> findMonthHappiness(NicknameAndDateDto nicknameAndDateDto) {
        return fillMonthHappinessAndDateDtoList(
                feedRepository.findHappinessAndDateDtoByNicknameAndDateDto(nicknameAndDateDto)
        );
    }

    private List<HappinessAndDateDto> fillMonthHappinessAndDateDtoList(List<HappinessAndDateDto> monthHappinessAndDateDtoList) {
        int lastDayOfMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        double[] check = new double[32];

        for (HappinessAndDateDto happinessAndDateDto : monthHappinessAndDateDtoList) {
            int day = Integer.parseInt(happinessAndDateDto.getFormattedDate());
            Double happiness = happinessAndDateDto.getHappiness();

            check[day] = happiness;
        }

        List<HappinessAndDateDto> filledList = new ArrayList<>();

        for(int i=1; i<=lastDayOfMonth; i++){
            filledList.add(
                    new HappinessAndDateDto(check[i],Integer.toString(i))
            );
        }

        return filledList;
    }

    public List<HappinessAndDateDto> findYearHappiness(NicknameAndDateDto nicknameAndDateDto) {
        List<HappinessAndDateDto> result = new ArrayList<>();

        String year = nicknameAndDateDto.getDate().toString().substring(0, 4);
        for (int i = 1; i <= 12; i++) {
            Long date = Long.parseLong(year + String.format("%02d", i) + "0000000000");

            Double happiness = feedRepository.findMonthHappinessAvgByNicknameAndDate(nicknameAndDateDto.getNickname(), date)
                    .orElse(0.0);

            result.add(new HappinessAndDateDto(happiness, date));
        }

        return result;
    }

    // ------------------------------------------------------------------------------- //

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
        feedRepository.findAll()
                .forEach(feed -> {
                    Integer happiness = feed.getHappiness();
                    List<String> categories = feed.getFeedCategories()
                            .stream()
                            .map(FeedCategory::getCategory)
                            .collect(Collectors.toList());

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
        feedCategoryRepository.findDistinctCategory()
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

    @SneakyThrows
    private void initCategoryAndSentenceMap() {
        InputStream inputStream = new ClassPathResource("category.txt").getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));

        String line = "";
        while ((line = buffer.readLine()) != null) {
            String[] categoryAndSentence = line.split("=");
            categoryAndSentenceMap.put(categoryAndSentence[0], Arrays.stream(categoryAndSentence[1].split(",")).toList());
        }
    }

    private List<String> convertCategoryToSentence(List<String> recommendCategoryList) {
        List<String> result = new ArrayList<>();
        
        for (int i = recommendCategoryList.size(), j = 0; i >= 1; i--, j++) {
            String category = recommendCategoryList.get(j);

            if(categoryAndSentenceMap.get(category) != null){
                List<String> sentenceList = new ArrayList<>(categoryAndSentenceMap.get(category));
                Collections.shuffle(sentenceList);
                result.addAll(sentenceList.subList(0, Math.min(sentenceList.size(), i * 3)));
            }
        }

        if(!result.isEmpty()){
            Collections.shuffle(result);
        }

        return result.isEmpty() ? List.of("피드 작성하기") : result.subList(0, Math.min(result.size(), 10));
    }
}
