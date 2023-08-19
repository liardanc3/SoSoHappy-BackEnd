package sosohappy.dmservice.exception.handler;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.http.codec.ServerCodecConfigurer;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Order(-2)
public class ResponseExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ResponseExceptionHandler(ResponseExceptionAttributes responseExceptionAttributes,
                                    ApplicationContext applicationContext,
                                    ServerCodecConfigurer serverCodecConfigurer) {
        super(responseExceptionAttributes, new Resources(), applicationContext);
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
                RequestPredicates.all(), this::responseException
        );
    }

    private Mono<ServerResponse> responseException(ServerRequest request) {

        Map<String, Object> errorMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        return ServerResponse.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorMap.get("error")));
    }
}