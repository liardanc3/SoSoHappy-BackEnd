package sosohappy.feedservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysisDto {

    List<String> bestCategoryList;
    List<String> recommendCategoryList;

}
