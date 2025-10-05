package com.example;
import com.example.api.ElpriserAPI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsolePrinter {
    // Används för att utföra beräkningar som behövs för utskrift (min, max, medel, timmedel)
    private final Calculator calculate;
    private final DateTimeFormatter HOURS = DateTimeFormatter.ofPattern("HH");
    private final DateTimeFormatter HOURS_MINUTES = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsolePrinter() {
        this.calculate = new Calculator();
    }

    public void printHeader() {
        System.out.println();
        System.out.println("╔════════════════════════════════╗");
        System.out.println("║     Electricity Price CLI      ║");
        System.out.println("╚════════════════════════════════╝\n");
    }

    public void printZone(ElpriserAPI.Prisklass zoneEnum) {
        System.out.println("------------------------------");
        System.out.println("Vald elpriszon: " + zoneEnum);
        System.out.println("------------------------------\n"); }

    // Skriver ut lägsta, högsta och medelpris för listan
    public void printMinMaxMean(List<ElpriserAPI.Elpris> prices) {
        ElpriserAPI.Elpris min = calculate.minPrice(prices);
        ElpriserAPI.Elpris max = calculate.maxPrice(prices);
        double mean = calculate.meanPrice(prices);

        String minDate = DATE.format(min.timeStart());
        String minStart = HOURS.format(min.timeStart());
        String minEnd = HOURS.format(min.timeStart().plusHours(1));

        String maxDate = DATE.format(max.timeStart());
        String maxStart = HOURS.format(max.timeStart());
        String maxEnd = HOURS.format(max.timeStart().plusHours(1));

        System.out.println("Lägsta pris:");
        System.out.println("Datum " + minDate + "   kl " + minStart + "-" + minEnd + "   pris: " + formatOre(min.sekPerKWh()) + " öre");
        System.out.println();
        System.out.println("Högsta pris:");
        System.out.println("Datum " + maxDate + "   kl " + maxStart + "-" + maxEnd + "   pris: " + formatOre(max.sekPerKWh()) + " öre");
        System.out.println();
        System.out.println("Medelpris: " + formatOre(mean) + " öre");
    }

    // Skriver ut medelpris per timme, sorterar om behövs
    public void printHourlyMeanPrice(List<ElpriserAPI.Elpris> collectedPrices, boolean sorted) {
        List<Integer> hoursOfDay = new ArrayList<>();
        List<Double> collectedMeanHours = calculate.hourlyMeanPrices(collectedPrices, hoursOfDay);
        if (sorted) calculate.sortDescending(collectedMeanHours, hoursOfDay);

        System.out.println();
        System.out.println("Timpriser (medel per timme):");
        for (int i = 0; i < hoursOfDay.size(); i++) {
            LocalTime start = LocalTime.of(hoursOfDay.get(i), 0);
            LocalTime end = start.plusHours(1);
            String timePeriod = start.format(HOURS) + "-" + end.format(HOURS);
            System.out.println(timePeriod + " " + formatOre(collectedMeanHours.get(i)) + " öre");
        }
    }

    // Skriver ut det billigaste laddningsfönstret med priser
    public void printCheapestWindow(List<ElpriserAPI.Elpris> cheapestWindow, int chargingHours) {
        System.out.println();
        System.out.println("Påbörja laddning under billigaste " + chargingHours + "-timmars laddningsfönstret:");

        for (ElpriserAPI.Elpris p : cheapestWindow) {
            String date = DATE.format(p.timeStart());
            String start = HOURS_MINUTES.format(p.timeStart());
            String end = HOURS_MINUTES.format(p.timeEnd());
            System.out.println("Datum " + date + "   kl " + start + "-" + end + "   pris " + formatOre(p.sekPerKWh()) + " öre");
        }
        System.out.println();
        System.out.println("Medelpris för fönster: " + formatOre(calculate.meanPrice(cheapestWindow)) + " öre");
    }

    // Formaterar pris till öre med två decimaler
    private String formatOre(double sekPerKWh) {
        double ore = sekPerKWh * 100.0;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(ore);
    }
}