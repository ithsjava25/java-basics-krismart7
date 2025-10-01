package com.example;

import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandLineArgs {

    private String zone;
    private String date;
    String charging;
    private boolean sorted = false;
    private boolean help = false;

    public CommandLineArgs(String[] args) {
        parseArgs(args);
    }

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
                    System.out.println(arg + " Unknown argument");
                    help = true;
                }
            }
        }
    }
    private String nextArg(String[] args, int i) {
        return (i + 1 < args.length) ? args[i + 1] : null;
    }

    public boolean isHelp() {return help || showArg(); }

    private String setArg(String value, String flag) {
        if (value == null) {
            System.out.println("Error: " + flag + " requires a value");
            help = true;
            return null;
        }
        return value;
    }

    private boolean showArg() {
        return zone == null && date == null && !sorted && charging == null;
    }

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

    public int getChargingHours() {
        if (charging == null) return 0;

        // Extrahera siffror
        String hoursStr = charging.replaceAll("\\D+", "");
        if (hoursStr.isEmpty()) {
            System.out.println("Invalid value for --charging: " + charging + ". Please enter 2, 4, or 8 hours.");
            help = true;
            return 0;
        }

        int hours = Integer.parseInt(hoursStr);

        // Validera att endast 2, 4 eller 8 tillåts
        if (hours != 2 && hours != 4 && hours != 8) {
            System.out.println("Invalid value for --charging: " + hours + ". Please enter 2, 4, or 8 hours.");
            help = true;
            return 0;
        }

        return hours;
    }

    public boolean isSorted() {return sorted; }
}