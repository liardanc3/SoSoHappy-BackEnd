package sosohappy.feedservice.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateFeedDto {

    @Size(min = 1, max = 10)
    private String nickname;

    @Min(value = 2000000000000000L)
    @Max(value = 9999999999999999L)
    private Long date;

    @NotEmpty
    private String weather;

    @NotEmpty
    private Integer happiness;

    private List<String> categoryList;

    private String text;

    private List<MultipartFile> imageList;

    @NotEmpty
    private Boolean isPublic;

}
