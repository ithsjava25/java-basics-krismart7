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
        // Initierar huvudkomponenter för programflödet: tar emot parser för kommandoradsargument,
        // skapar API-provider, kalkylator och utskriftsfunktion
        this.parser = parser;
        ElpriserAPI api = new ElpriserAPI();
        this.priceProvider = new ElpriceProvider(api);
        this.calc = new Calculator();
        this.printer = new ConsolePrinter();
    }

    public void execute() {
        // Visa hjälptext vid exekvering av program om inga argument eller --help används
        if (parser.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Läs in användarens val av zon, datum och laddningstimmar
        ElpriserAPI.Prisklass zoneEnum = parser.getZone();
        LocalDate chosenDate = parser.getDate();
        int chargingHours = parser.getChargingHours();

        // Kontrollera argumentfel och visa hjälp vid behov
        if (parser.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Hämta elpriser från API för valda datum och zon
        List<ElpriserAPI.Elpris> collectedPrices = priceProvider.getCollectedPrices(chosenDate, zoneEnum);

        // Avsluta om inga priser finns
        if (priceProvider.noPrices(collectedPrices, chosenDate, zoneEnum)) {
            return;
        }

        // Visa beräknade timpriser, vald zon och prisstatistik
        printer.printHeader();
        printer.printZone(zoneEnum);
        printer.printMinMaxMean(collectedPrices);
        printer.printHourlyMeanPrice(collectedPrices, parser.isSorted());

        // Beräkna och visa billigaste laddningsfönster om laddningstimmar anges
        if (parser.getChargingHours() > 0) {
            List<ElpriserAPI.Elpris> cheapestWindow = calc.findCheapestWindow(collectedPrices, chargingHours);
            printer.printCheapestWindow(cheapestWindow, chargingHours);
        }
        // Visa hjälptext som referens efter exekvering
        ConsoleHelp.showHelp();
    }
}