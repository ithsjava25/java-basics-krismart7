package com.example;
import com.example.api.ElpriserAPI;
import com.example.utils.ConsoleHelp;
import java.time.LocalDate;
import java.util.List;

public class AppRunner {
    private final CommandLineArgs cliArgs;
    private final ElPriceService elService;
    private final Calculator calculate;
    private final Printer print;

    public AppRunner(CommandLineArgs cliArgs) {
        this.cliArgs = cliArgs;
        ElpriserAPI api = new ElpriserAPI();
        this.elService = new ElPriceService(api);
        this.calculate = new Calculator();
        this.print = new Printer();
    }

    public void run() {
        if (cliArgs.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Läs zon
        ElpriserAPI.Prisklass zoneEnum = cliArgs.getZone();
        // Läs datum
        LocalDate chosenDate = cliArgs.getDate();
        // Läs laddningstimmar
        int chargingHours = cliArgs.getChargingHours();

        // Om något värde var fel
        if (cliArgs.isHelp()) {
            ConsoleHelp.showHelp();
            return;
        }

        // Hämta priser
        List<ElpriserAPI.Elpris> collectedPrices = elService.getCollectedPrices(chosenDate, zoneEnum);

        // Kontrollera om det finns priser
        if (elService.noPrices(collectedPrices, zoneEnum, chosenDate)) {
            return;
        }

        // Utskrifter
        print.printHourlyMeanPrice(collectedPrices, cliArgs.isSorted());
        print.printZone(zoneEnum);
        print.printMinMaxMean(collectedPrices);

        // Laddnings-fönster
        if (chargingHours > 0) {
            List<ElpriserAPI.Elpris> window = calculate.findCheapestWindow(collectedPrices, chargingHours);
            print.printCheapestWindow(window, chargingHours);
        }
        ConsoleHelp.showHelp();
    }
}