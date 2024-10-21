package command;


import app.ConsoleApp;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "help : вывести справку по доступным командам");
    }

    @Override
    public void execute(String argument) {
        for (String i : Command.commandList.keySet()) {
            if (i.equals("register") || i.equals("login")){
                continue;
            }
            ConsoleApp.commandPrint(Command.commandList.get(i).getHelpInfo());
        }
    }
}
