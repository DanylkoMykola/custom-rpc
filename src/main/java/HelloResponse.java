import java.io.Serializable;

public class HelloResponse implements Serializable {
    private String message;

    public HelloResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

}
