package dto;

import java.io.Serializable;

public class Response implements Serializable {
    private final String message;
    private final ExecutionStatus status;

    public Response(String message, ExecutionStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public ExecutionStatus getStatus() {
        return status;
    }
}
