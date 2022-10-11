package pe.edu.unmsm.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ValidationErrorException extends RuntimeException {
    private final HttpStatus status;
    private final List<ValidationError> validationErrors;

    public ValidationErrorException(List<ValidationError> validationErrors) {
        super();
        this.status = HttpStatus.BAD_REQUEST;
        this.validationErrors = validationErrors;
    }
}
