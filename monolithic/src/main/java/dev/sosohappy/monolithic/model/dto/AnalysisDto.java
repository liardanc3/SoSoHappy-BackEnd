package dev.sosohappy.monolithic.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysisDto {

    List<String> bestCategoryList;

    @JsonProperty(value = "recommendCategoryList")
    List<String> recommendCategorySentenceList;

}
