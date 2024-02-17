package storeApi;

public class NotValidStoreIdResponse {

    private int code;
    private String type;
    private String message;

    public NotValidStoreIdResponse(){}

    public NotValidStoreIdResponse(int code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
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
