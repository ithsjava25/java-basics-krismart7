package com.example;

public class Main {
    public static void main(String[] args) {

        // Tar emot kommandoradsargument och initierar parser för att hantera dem
        CommandLineParser parser = new CommandLineParser(args);

        // Skapar ExecutionFlow med parsern för att hantera programmets logik och argument
        ExecutionFlow flow = new ExecutionFlow(parser);

        // Exekverar programmet: hämtar priser, beräknar statistik och visar resultat
        flow.execute();
    }
}