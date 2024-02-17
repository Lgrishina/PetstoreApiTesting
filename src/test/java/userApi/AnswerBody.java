package userApi;

public class AnswerBody {
    private int code;
    private String type;
    private String message;


    public AnswerBody(int code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public AnswerBody() {
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }


}
