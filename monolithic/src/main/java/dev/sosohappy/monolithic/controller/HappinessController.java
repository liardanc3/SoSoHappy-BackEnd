package dev.sosohappy.monolithic.controller;

import dev.sosohappy.monolithic.model.dto.AnalysisDto;
import dev.sosohappy.monolithic.model.dto.HappinessAndDateDto;
import dev.sosohappy.monolithic.model.dto.NicknameAndDateDto;
import dev.sosohappy.monolithic.service.HappinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/feed-service")
@RestController
@RequiredArgsConstructor
public class HappinessController {

    private final HappinessService happinessService;

    @PostMapping("/analysisHappiness")
    public AnalysisDto analysisHappiness(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return happinessService.analysisHappiness(nicknameAndDateDto);
    }

    @PostMapping("/findMonthHappiness")
    public List<HappinessAndDateDto> findMonthHappiness(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return happinessService.findMonthHappiness(nicknameAndDateDto);
    }

    @PostMapping("/findYearHappiness")
    public List<HappinessAndDateDto> findYearHappiness(@ModelAttribute @Valid NicknameAndDateDto nicknameAndDateDto){
        return happinessService.findYearHappiness(nicknameAndDateDto);
    }
}
