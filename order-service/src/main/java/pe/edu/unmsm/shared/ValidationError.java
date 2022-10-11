package pe.edu.unmsm.shared;

import java.io.Serializable;

public record ValidationError(String field, String message) implements Serializable {
}
