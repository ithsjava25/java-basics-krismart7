package com.example;

public class Main {
    public static void main(String[] args) {

        CommandLineParser parser = new CommandLineParser(args);
        ExecutionFlow flow = new ExecutionFlow(parser);
        flow.execute();
    }
}