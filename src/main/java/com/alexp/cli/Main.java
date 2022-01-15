package com.alexp.cli;

import com.github.rvesse.airline.annotations.Cli;
import com.github.rvesse.airline.help.Help;
import com.github.rvesse.airline.parser.errors.ParseException;

import java.util.Arrays;

@Cli(name = "nasacli", defaultCommand = Help.class, commands = {MarsPhotosCommand.class, Help.class})
public class Main {

    public static void main(String[] args) {
        try {
            var cli = new com.github.rvesse.airline.Cli<Runnable>(Main.class);
            var parseResult = cli.parseWithResult(args);
            if (parseResult.wasSuccessful()) {
                parseResult.getCommand().run();
                System.exit(0);
            } else {
                System.err.println(String.format("%d errors encountered:", parseResult.getErrors().size()));
                int i = 1;
                for (ParseException e : parseResult.getErrors()) {
                    System.err.println(String.format("Error %d: %s", i, e.getMessage()));
                    i++;
                }
                System.err.println();
                Help.help(cli.getMetadata(), Arrays.asList(args), System.err);
            }
        } catch (Exception e) {
            System.err.println(String.format("Unexpected error: %s", e.getMessage()));
            System.err.println("type 'help <command>' or 'help' to see the proper usage");
        }
        System.exit(1);
    }
}
