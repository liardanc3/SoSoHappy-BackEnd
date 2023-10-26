package sosohappy.feedservice.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameAndDateDto {

    @Size(min = 1, max = 10)
    private String nickname;

    @Min(value = 2000000000000000L)
    @Max(value = 9999999999999999L)
    private Long date;
}
