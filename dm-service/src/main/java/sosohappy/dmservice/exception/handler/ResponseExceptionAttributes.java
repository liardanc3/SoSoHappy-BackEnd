package sosohappy.dmservice.exception.handler;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import sosohappy.dmservice.exception.custom.FindMessageException;
import sosohappy.dmservice.domain.dto.ExceptionDto;

import java.util.Map;

@Component
public class ResponseExceptionAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {

        Throwable error = getError(request);

        if(error instanceof FindMessageException){
            return Map.of(
                    "error", new ExceptionDto("메시지 조회 실패"),
                    "status", 404
            );
        }

        return Map.of(
                "error", new ExceptionDto("알 수 없는 오류"),
                "status", 500
        );
    }

}