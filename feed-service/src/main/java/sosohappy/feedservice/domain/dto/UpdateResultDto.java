package sosohappy.feedservice.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateResultDto {

    private Boolean success;
    private String message;

    public static UpdateResultDto updateSuccess(String message){
        return UpdateResultDto.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static UpdateResultDto updateFailure(String message){
        return UpdateResultDto.builder()
                .success(false)
                .message(message)
                .build();
    }

}
