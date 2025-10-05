package com.example;
import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandLineParser {
    // Sparar värden från kommandoradsargument
    private String zone;
    private String date;
    private String charging;
    private boolean sorted = false;
    private boolean help = false;

    // Konstruktor som tar emot kommandoradsargument och parser dem
    public CommandLineParser(String[] args) {
        parseArgs(args);
    }

    // Parse:a argument och spara dem i fälten
    private void parseArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            switch (arg) {
                case "--zone" -> zone = setArg(nextArg(args, i++), "--zone");
                case "--date" -> date = setArg(nextArg(args, i++), "--date");
                case "--charging" -> charging = setArg(nextArg(args, i++), "--charging");
                case "--sorted" -> sorted = true;
                case "--help" -> help = true;
                default -> {
                    System.out.println(arg + " is an unknown argument");
                    help = true;
                }
            }
        }
    }

    // Hämtar nästa argument i arrayen
    private String nextArg(String[] args, int i) {
        return (i + 1 < args.length) ? args[i + 1] : null;
    }

    // Kontrollera om hjälptext ska visas
    public boolean isHelp() { return help || zone == null && date == null && !sorted && charging == null; }

    // Validera och spara argumentvärde, visa fel om saknas
    private String setArg(String argValue, String flag) {
        if (argValue == null) {
            System.out.println("Error: " + flag + " requires a value");
            help = true;
            return null;
        }
        return argValue;
    }

    // Konvertera zonsträng till enum, hantera ogiltig zon
    public ElpriserAPI.Prisklass getZone() {
        if (zone == null) {
            System.out.println("Error: --zone is required");
            help = true;
            return null;
        }
        try {
            return ElpriserAPI.Prisklass.valueOf(zone.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid zone: " + zone);
            help = true;
            return null;
        }
    }

    // Konvertera datumsträng till LocalDate, default till idag om null
    public LocalDate getDate() {
        if (date == null) return LocalDate.now();
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date: " + date);
            help = true;
            return null;
        }
    }

    // Omvandla sträng från kommandoradsargument till heltal och validera mot tillåtna laddningstimmar
    public int getChargingHours() {
        if (charging == null) return 0;
        try {
            int hours = Integer.parseInt(charging.replaceAll("\\D+", ""));
            if (validCharging(hours)) return hours;
        } catch (NumberFormatException ignored) {
        }
        return invalidCharging(charging);
    }

    // Kontrollera om laddningstimmar är giltiga
    private boolean validCharging(int hours) { return hours == 2 || hours == 4 || hours == 8; }

    // Hantera ogiltig laddningstid och visa felmeddelande
    private int invalidCharging(String argValue) {
        System.out.println("Invalid value for --charging: " + argValue + ". Please enter 2, 4, or 8 hours.");
        help = true;
        return 0;
    }
    // Returnera om sortering ska användas
    public boolean isSorted() { return sorted; }
}