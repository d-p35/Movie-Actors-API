package ca.utoronto.utm.mcs;

public class UserException extends Exception{
    String message;

    public UserException(String message){
        super(message);
        this.message = message;
    }
}
