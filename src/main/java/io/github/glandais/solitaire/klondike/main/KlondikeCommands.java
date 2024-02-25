package io.github.glandais.solitaire.klondike.main;

import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "klondike", mixinStandardHelpOptions = true, subcommands = {Debug.class,
        Print.class, Solve.class, Play.class})
public class KlondikeCommands implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }
}
