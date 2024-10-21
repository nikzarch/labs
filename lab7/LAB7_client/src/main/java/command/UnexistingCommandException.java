package command;


public class UnexistingCommandException extends Exception {

    public UnexistingCommandException(String message) {
        super(message);
    }
}
