package sosohappy.feedservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sosohappy.feedservice.domain.dto.AnalysisDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.repository.FeedRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class HappinessService {

    private final ConcurrentHashMap<String, Integer> categoryToIndexMap;
    private final AtomicReference<List<List<Integer>>> similarityMatrix;
    private final FeedRepository feedRepository;

    @PostConstruct
    void updateSimilarityMatrix() {
        int size = (int) feedRepository.findAllCategories()
                .stream()
                .map(category -> categoryToIndexMap.putIfAbsent(category, categoryToIndexMap.size()))
                .count();

        List<List<Integer>> initMatrix = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            initMatrix.add(new ArrayList<>(Collections.nCopies(size, 0)));
        }

        feedRepository.findHappinessDtoAll()
                .forEach(happinessDto -> {
                    List<String> categories = happinessDto.getCategories();

                    for (int i = 0; i < categories.size(); i++){

                        int srcIdx = categoryToIndexMap.get(categories.get(i));

                        for (int j = 0; j < categories.size(); j++){
                            if (i != j){

                                int dstIdx = categoryToIndexMap.get(categories.get(j));

                                initMatrix.get(srcIdx)
                                        .set(
                                                dstIdx,
                                                initMatrix.get(srcIdx).get(dstIdx) + happinessDto.getHappiness()
                                        );
                            }
                        }
                    }
                });

        similarityMatrix.set(initMatrix);
    }

    public AnalysisDto analysisHappiness(NicknameAndDateDto nicknameAndDateDto) {
        return new AnalysisDto();
    }
}
