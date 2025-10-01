package com.example;

public class Main {
    public static void main(String[] args) {

        CommandLineArgs cliArgs = new CommandLineArgs(args);
        AppRunner runner = new AppRunner(cliArgs);
        runner.run();
    }
}