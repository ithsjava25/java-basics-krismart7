package com.example;
import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.util.List;

public class ExecutionFlow {
    private final CommandLineParser parser;
    private final ElpriceProvider priceProvider;
    private final Calculator calc;
    private final ConsolePrinter printer;

    public ExecutionFlow(CommandLineParser parser) {
        this.parser = parser;
        ElpriserAPI api = new ElpriserAPI();
        this.priceProvider = new ElpriceProvider(api);
        this.calc = new Calculator();
        this.printer = new ConsolePrinter();
    }

    public void execute() {
        if (parser.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Läs zon
        ElpriserAPI.Prisklass zoneEnum = parser.getZone();
        // Läs datum
        LocalDate chosenDate = parser.getDate();
        // Läs laddningstimmar
        int chargingHours = parser.getChargingHours();

        // Om något värde var fel
        if (parser.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Hämta priser
        List<ElpriserAPI.Elpris> collectedPrices = priceProvider.getCollectedPrices(chosenDate, zoneEnum);

        // Kontrollera om det finns priser
        if (priceProvider.noPrices(collectedPrices, chosenDate, zoneEnum)) {
            return;
        }

        // Utskrifter
        printer.printHourlyMeanPrice(collectedPrices, parser.isSorted());
        printer.printZone(zoneEnum);
        printer.printMinMaxMean(collectedPrices);

        // Laddnings-fönster
        if (parser.getChargingHours() > 0) {
            List<ElpriserAPI.Elpris> cheapestWindow = calc.findCheapestWindow(collectedPrices, chargingHours);
            printer.printCheapestWindow(cheapestWindow, chargingHours);
        }
        ConsoleHelp.showHelp();
    }
}