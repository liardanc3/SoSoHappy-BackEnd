package sosohappy.feedservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sosohappy.feedservice.domain.dto.AnalysisDto;
import sosohappy.feedservice.domain.dto.DayHappinessAndDateDto;
import sosohappy.feedservice.domain.dto.NicknameAndDateDto;
import sosohappy.feedservice.service.HappinessService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HappinessController {

    private final HappinessService happinessService;

    @PostMapping("/analysisHappiness")
    public AnalysisDto analysisHappiness(@ModelAttribute NicknameAndDateDto nicknameAndDateDto){
        return happinessService.analysisHappiness(nicknameAndDateDto);
    }

    @PostMapping("/findMonthHappiness")
    public List<DayHappinessAndDateDto> findMonthHappiness(@ModelAttribute NicknameAndDateDto nicknameAndDateDto){
        return happinessService.findMonthHappiness(nicknameAndDateDto);
    }
}
