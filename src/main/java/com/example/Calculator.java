package com.example;
import com.example.api.ElpriserAPI;
import java.util.ArrayList;
import java.util.List;


public class Calculator {

    public record HourPrice(int hour, double price) {}

    public double minPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return 0.0;

        double min = prices.get(0).sekPerKWh();
        for (ElpriserAPI.Elpris p : prices) {
            if (p.sekPerKWh() < min) {
                min = p.sekPerKWh();
            }
        } return min;
    }

    public double maxPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return 0.0;

        double max = prices.get(0).sekPerKWh();
        for (ElpriserAPI.Elpris p : prices) {
            if (p.sekPerKWh() > max) {
                max = p.sekPerKWh();
            }
        } return max;
    }

    public double meanPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return 0.0;

        double sum = 0.0;
        for (ElpriserAPI.Elpris p : prices) {
            sum += p.sekPerKWh();
        } return sum / prices.size();
    }

    public List<HourPrice> getHourlyMeanPrices(List<ElpriserAPI.Elpris> prices, boolean sorted) {
        // Skapar en lista som ska innehålla HourPrice-records (timme + medelpris)
        List<HourPrice> hourlyPrices = new ArrayList<>();
        if (prices.isEmpty()) return hourlyPrices;

        // Börja med timmen för första pris
        int currentHour = prices.get(0).timeStart().getHour();
        // Lista för att samla alla priser som tillhör samma timme
        List<ElpriserAPI.Elpris> pricesInHour = new ArrayList<>();

        for (ElpriserAPI.Elpris price : prices) {
            int priceHour = price.timeStart().getHour();

            // Om timmen ändras till nästa timme
            if (priceHour != currentHour) {
                // Beräkna medelpriset för föregående timme
                double mean = meanPrice(pricesInHour);
                // Skapa ett HourPrice-record och lägg till i listan
                hourlyPrices.add(new HourPrice(currentHour, mean));
                // Börja samla priser för nästa timme
                pricesInHour = new ArrayList<>();
                currentHour = priceHour;
            }
            // Lägg till priset i listan för aktuell timme
            pricesInHour.add(price);
        }
        // När loopen är klar, lägg till sista timmens medelpris
        double mean = meanPrice(pricesInHour);
        hourlyPrices.add(new HourPrice(currentHour, mean));
        // Om sortering önskas, sortera listan fallande på medelpris
        if (sorted) sortDescending(hourlyPrices);
        // Returnera listan med HourPrice-records
        return hourlyPrices;
    }

    private void sortDescending(List<HourPrice> hourlyPrices) {

        for (int i = 0; i < hourlyPrices.size() - 1; i++) {
            for (int j = 0; j < hourlyPrices.size() - 1 - i; j++) {
                if (hourlyPrices.get(j).price() < hourlyPrices.get(j + 1).price()) {
                    HourPrice temp = hourlyPrices.get(j);
                    hourlyPrices.set(j, hourlyPrices.get(j + 1));
                    hourlyPrices.set(j + 1, temp);
                }
            }
        }
    }

    public List<ElpriserAPI.Elpris> findCheapestWindow(List<ElpriserAPI.Elpris> prices, int windowHours) {
        List<ElpriserAPI.Elpris> cheapestWindow = new ArrayList<>();

        if (prices.isEmpty() || windowHours <= 0 || prices.size() < windowHours) {
            return cheapestWindow;
        }
        int totalPrices = prices.size();
        double minSum = 0;

        // Beräkna summan av de första windowHours timmarna
        for (int i = 0; i < windowHours; i++) {
            minSum += prices.get(i).sekPerKWh();
        }

        int startIndex = 0;

        // Index där det billigaste fönstret börjar
        for (int start = 1; start < totalPrices; start++) {
            double sum = 0;
            for (int i = 0; i < windowHours; i++) {
                int index = (start + i) % totalPrices;
                sum += prices.get(index).sekPerKWh();
            }
            // Om denna summa är billigare, spara startindex och nytt minSum
            if (sum < minSum) {
                minSum = sum;
                startIndex = start;
            }
        }
        // Bygg listan med de billigaste priserna utifrån startIndex
        for (int i = 0; i < windowHours; i++) {
            int index = (startIndex + i) % totalPrices;
            cheapestWindow.add(prices.get(index));
        }
        return cheapestWindow;
    }
}