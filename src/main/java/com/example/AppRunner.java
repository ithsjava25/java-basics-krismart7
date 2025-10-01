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
        if (zoneEnum == null) {
            ConsoleHelp.showHelp();
            return;
        }

        // Läs datum
        LocalDate chosenDate = cliArgs.getDate();
        if (chosenDate == null) {
            ConsoleHelp.showHelp();
            return;
        }

        // Läs laddningstimmar
        int chargingHours = cliArgs.getChargingHours();
        if (chargingHours == 0 && cliArgs.charging != null) { // ogiltigt värde
            ConsoleHelp.showHelp();
            return; // stoppar programmet här
        }

        // Hämta priser
        List<ElpriserAPI.Elpris> collectedPrices = elService.getCollectedPrices(chosenDate, zoneEnum);

        // Kontrollera om det finns priser
        if (elService.noPrices(collectedPrices, zoneEnum.name(), chosenDate)) {
            return;
        }

        // Utskrifter
        print.printHourlyMeanPrice(collectedPrices, cliArgs.isSorted());
        print.printZone(zoneEnum);
        print.printMinMaxMean(collectedPrices);

        // Laddnings-fönster (endast giltiga värden)
        if (chargingHours > 0) {
            List<ElpriserAPI.Elpris> window = calculate.findCheapestWindow(collectedPrices, chargingHours);
            print.printCheapestWindow(window, chargingHours);
        }
        ConsoleHelp.showHelp();
    }
}