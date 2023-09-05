package sosohappy.feedservice.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisDto {

    List<String> bestHappiness;
    List<String> recommendHappiness;

}
