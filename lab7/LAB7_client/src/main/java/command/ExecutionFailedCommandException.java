package command;

public class ExecutionFailedCommandException extends Exception{
    public ExecutionFailedCommandException(String message){
        super(message);
    }
}
