package io.github.glandais.solitaire;

import io.github.glandais.solitaire.klondike.main.KlondikeCommands;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "solitaire", mixinStandardHelpOptions = true, subcommands = KlondikeCommands.class)
public class Main implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

}
