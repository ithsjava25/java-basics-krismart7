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
    private final Calculator calculate;
    private final DateTimeFormatter HOURS = DateTimeFormatter.ofPattern("HH");
    private final DateTimeFormatter HOURS_MINUTES = DateTimeFormatter.ofPattern("HH:mm");

    public ConsolePrinter() {
        this.calculate = new Calculator();
    }

    public void printZone(ElpriserAPI.Prisklass zoneEnum) {
        System.out.println();
        System.out.println("Vald elpriszon: " + zoneEnum); }

    public void printMinMaxMean(List<ElpriserAPI.Elpris> prices) {
        System.out.println("Lägsta pris: " + formatOre(calculate.minPrice(prices)) + " öre");
        System.out.println("Högsta pris: " + formatOre(calculate.maxPrice(prices)) + " öre");
        System.out.println("Medelpris: " + formatOre(calculate.meanPrice(prices)) + " öre");
    }

    public void printHourlyMeanPrice(List<ElpriserAPI.Elpris> prices, boolean sorted) {
        List<Integer> hours = new ArrayList<>();
        List<Double> means = calculate.hourlyMeanPrices(prices, hours);
        if (sorted) calculate.sortDescending(hours, means);

        for (int i = 0; i < hours.size(); i++) {
            LocalTime start = LocalTime.of(hours.get(i), 0);
            LocalTime end = start.plusHours(1);
            String timePeriod = start.format(HOURS) + "-" + end.format(HOURS);
            System.out.println(timePeriod + " " + formatOre(means.get(i)) + " öre");
        }
    }

    public void printCheapestWindow(List<ElpriserAPI.Elpris> cheapestWindow, int windowHours) {
        System.out.println();
        System.out.println("Påbörja laddning under billigaste " + windowHours + "-timmarsfönstret:");

        for (ElpriserAPI.Elpris pris : cheapestWindow) {
            String start = pris.timeStart().format(HOURS_MINUTES);
            String end = pris.timeEnd().format(HOURS_MINUTES);
            System.out.println("kl " + start + "-" + end + " " + formatOre(pris.sekPerKWh()) + " öre");
        }
        System.out.println();
        System.out.println("Medelpris för fönster: " + formatOre(calculate.meanPrice(cheapestWindow)) + " öre");
        System.out.println();
    }

    private String formatOre(double sekPerKWh) {
        double ore = sekPerKWh * 100.0;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("sv", "SE"));
        DecimalFormat df = new DecimalFormat("0.00", symbols);
        return df.format(ore);
    }
}