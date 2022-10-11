package pe.edu.unmsm.shared;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestValidator {
    private final Validator validator;

    public <T> void validate(T t) {
        List<ValidationError> validationErrors = validator.validate(t).stream().map(this::buildValidationError).toList();
        if (!validationErrors.isEmpty()) throw new ValidationErrorException(validationErrors);
    }

    private <T> ValidationError buildValidationError(ConstraintViolation<T> constraintViolation) {
        return new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
    }
}
