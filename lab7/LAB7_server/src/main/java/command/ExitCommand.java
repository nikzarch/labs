package command;

public class
ExitCommand extends Command {

    public ExitCommand() {
        super("exit", "exit : завершить программу (без сохранения в файл)");
    }

    @Override
    public void execute(String argument) {}
}
