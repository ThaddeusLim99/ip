package duke.parser;

import duke.command.*;
import duke.exception.*;

/**
 * Parser class gets the raw input from Logic class and then returns the specified arguments of interest
 * to the Logic class. These are the type of command, the description of the dask and the date.
 *
 * @param "input" raw input from user.
 * @return appropriate information depending on the method called.
 */
public class Parser {

    public Command parseCommand(String userInput) throws DukeException{
        String[] words = userInput.trim().split(" ", 2);  // split the input into command and arguments
        if (words.length == 0) {
            return new IncorrectCommand();
        }

        final String commandWord = words[0];
        final String arguments = userInput.replaceFirst(commandWord, "").trim();

        switch (commandWord) {
        case "todo":
        case "find":
            return prepareToDoOrFindCommand(commandWord, arguments);

        case "event":
        case "deadline":
            return prepareEventOrDeadlineCommand(commandWord, arguments);

        case "delete":
        case "done":
            return prepareDeleteOrDoneCommand(commandWord, arguments);

        case "list":
            return new ListCommand();

        case "bye":
            return new ExitCommand();

        default:
            return new IncorrectCommand();
        }
    }

    private Command prepareToDoOrFindCommand(String command, String args) throws  DukeException{
        if (args.isEmpty()) {
            if (command.equals("todo")) {
                throw new ToDoCommandError();
            }
            throw new FindCommandError();
        }
        try {
            if (command.equals("todo")) {
                return new ToDoCommand(args.trim());
            }
            return new FindCommand(args.trim());
        } catch (Exception e) {
            if (command.equals("todo")) {
                throw new ToDoCommandError();
            }
            throw new FindCommandError();
        }
    }

    private Command prepareEventOrDeadlineCommand(String command, String args) throws DukeException{
        String[] parts = args.split("/", 2);
         //Validate arg string format
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            if(command.equals("event")){
                throw  new EventCommandError();
            }
            throw  new DeadLineCommandError();
        }
        if (command.equals("event") && isWrongEscapeWord(parts[1],"at")) {
            throw new EventCommandError();
        }
        if (command.equals("deadline") && isWrongEscapeWord(parts[1],"by")) {
            throw new DeadLineCommandError();
        }
        try {
            String date = getDate(parts[1]);
            if(command.equals("event")) {
                return new EventCommand(
                        parts[0].trim(),
                        date.trim()
                );
            }
            return new DeadlineCommand(
                    parts[0].trim(),
                    parts[1].trim()
            );
        } catch (Exception e) {
            if (command.equals("event")) {
                throw  new EventCommandError();
            }
            throw  new DeadLineCommandError();
        }
    }

    private Command prepareDeleteOrDoneCommand(String command, String args) throws  DukeException{
        try {
            final int targetIndex = Integer.parseInt(args.split(" ")[0]);
            if (command.equals("delete")) {
                return new DeleteCommand(targetIndex);
            }
            return new DoneCommand(targetIndex);
        } catch (NumberFormatException nfe) {
            if (command.equals("delete")) {
                throw  new DeleteCommandError();
            }
            throw  new DoneCommandError();
        }
    }

    public String getDate(String input) throws DukeException{
        String date = input;
        int firstWhitespace = getFirstWhiteSpace(date);
        if (firstWhitespace == -1) {
            throw new DateError();
        }
        date = input.substring(firstWhitespace);
        return date;
    }

    public int getFirstWhiteSpace(String input) {
        for (int index = 0; index < input.length(); index++) {
            //sees if character at that index is a whitespace
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    public boolean isWrongEscapeWord(String inputString, String escapeWord) {
        if (inputString.substring(0,2).equals(escapeWord)) {
            return false;
        }
        return true;
    }
}