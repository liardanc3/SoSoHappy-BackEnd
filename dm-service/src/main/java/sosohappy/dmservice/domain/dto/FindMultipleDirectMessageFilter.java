package sosohappy.dmservice.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FindMultipleDirectMessageFilter {

    @NotEmpty
    private String sender;
}
