package app.domain.services;

public class BusinessException {

    private final String message;
    public BusinessException(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}

