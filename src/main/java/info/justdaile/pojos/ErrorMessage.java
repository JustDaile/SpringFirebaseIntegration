package info.justdaile.pojos;

public class ErrorMessage extends BasicMessage {

    private ErrorType errorType;

    public ErrorMessage(String message, ErrorType errorType){
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

}
