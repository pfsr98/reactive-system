package pe.edu.unmsm.shared;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        errorAttributes.replace("timestamp", LocalDateTime.now());
        if (error instanceof ValidationErrorException validationErrorException) {
            errorAttributes.put("error", validationErrorException.getStatus().getReasonPhrase());
            errorAttributes.put("status", validationErrorException.getStatus().value());
            errorAttributes.put("validationErrors", validationErrorException.getValidationErrors());
        } else {
            errorAttributes.put("message", error.getMessage());
        }
        return errorAttributes;
    }
}
