package com.example;
import com.example.api.ElpriserAPI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Printer {
    private final Calculator calculate;
    private final DateTimeFormatter hours = DateTimeFormatter.ofPattern("HH");
    private final DateTimeFormatter hoursandminutes = DateTimeFormatter.ofPattern("HH:mm");

    public Printer() {
        this.calculate = new Calculator();
    }

    public void printZone(ElpriserAPI.Prisklass prisklass) {
        System.out.println();
        System.out.println("Vald elpriszon: " + prisklass.name()); }

    public void printMinMaxMean(List<ElpriserAPI.Elpris> priser) {
        System.out.println("Lägsta pris: " + formatOre(calculate.minPrice(priser)) + " öre");
        System.out.println("Högsta pris: " + formatOre(calculate.maxPrice(priser)) + " öre");
        System.out.println("Medelpris: " + formatOre(calculate.meanPrice(priser)) + " öre");
    }

    public void printHourlyMeanPrice(List<ElpriserAPI.Elpris> prices, boolean sorted) {
        List<Integer> meanHours = new ArrayList<>();
        List<Double> meanPrices = calculate.hourlyMeanPrice(prices, meanHours);
        if (sorted) calculate.sortDescending(meanHours, meanPrices);

        for (int i = 0; i < meanHours.size(); i++) {
            LocalTime start = LocalTime.of(meanHours.get(i), 0);
            LocalTime end = start.plusHours(1);

            String timePeriod = start.format(hours) + "-" + end.format(hours);
            System.out.println(timePeriod + " " + formatOre(meanPrices.get(i)) + " öre");
        }
    }

    public void printCheapestWindow(List<ElpriserAPI.Elpris> cheapestWindow, int windowHours) {
        System.out.println();
        System.out.println("Påbörja laddning under billigaste " + windowHours + "-timmarsfönstret:");

        for (ElpriserAPI.Elpris pris : cheapestWindow) {
            String start = pris.timeStart().format(hoursandminutes);
            String end = pris.timeEnd().format(hoursandminutes);
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