package com.example;
import com.example.api.ElpriserAPI;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

    // Returnerar det elprisobjekt med lägsta pris från listan
    public ElpriserAPI.Elpris minPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return null;
        ElpriserAPI.Elpris min = prices.get(0);
        for (ElpriserAPI.Elpris p : prices) {
            if (p.sekPerKWh() < min.sekPerKWh()) {
                min = p;
            }
        }
        return min;
    }

    // Returnerar det elprisobjekt med högsta pris från listan
    public ElpriserAPI.Elpris maxPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return null;
        ElpriserAPI.Elpris max = prices.get(0);
        for (ElpriserAPI.Elpris p : prices) {
            if (p.sekPerKWh() > max.sekPerKWh()) {
                max = p;
            }
        }
        return max;
    }

    // Beräknar medelpriset av alla elprisobjekt i listan
    public double meanPrice(List<ElpriserAPI.Elpris> prices) {
        if (prices.isEmpty()) return 0.0;
        double sum = 0.0;
        for (ElpriserAPI.Elpris p : prices) {
            sum += p.sekPerKWh();
        }
        return sum / prices.size();
    }

    // Beräknar medelpris per timme och fyller listan med timmar
    public List<Double> hourlyMeanPrices(List<ElpriserAPI.Elpris> collectedPrices, List<Integer> hoursOfDay) {
        List<Double> collectedMeanHours = new ArrayList<>();
        if (collectedPrices.isEmpty()) return collectedMeanHours;

        int currentHourValue = collectedPrices.get(0).timeStart().getHour();
        List<ElpriserAPI.Elpris> pricesInHour = new ArrayList<>();

        for (ElpriserAPI.Elpris price : collectedPrices) {
            int hour = price.timeStart().getHour();
            if (hour != currentHourValue) {
                hoursOfDay.add(currentHourValue);
                double mean = meanPrice(pricesInHour);
                collectedMeanHours.add(mean);
                pricesInHour = new ArrayList<>();
                currentHourValue = hour;
            }
            pricesInHour.add(price);
        }
        hoursOfDay.add(currentHourValue);
        double mean = meanPrice(pricesInHour);
        collectedMeanHours.add(mean);
        return collectedMeanHours;
    }

    // Sorterar listan med medelpriser i fallande ordning och justerar timmarna i samma ordning
    public void sortDescending(List<Double> collectedMeanHours, List<Integer> hoursOfDay) {
        for (int i = 0; i < collectedMeanHours.size() - 1; i++) {
            for (int j = 0; j < collectedMeanHours.size() - 1 - i; j++) {

                if (collectedMeanHours.get(j) < collectedMeanHours.get(j + 1)) {

                    double tempPrice = collectedMeanHours.get(j);
                    collectedMeanHours.set(j, collectedMeanHours.get(j + 1));
                    collectedMeanHours.set(j + 1, tempPrice);

                    int tempHour = hoursOfDay.get(j);
                    hoursOfDay.set(j, hoursOfDay.get(j + 1));
                    hoursOfDay.set(j + 1, tempHour);
                }
            }
        }
    }

    // Hittar det billigaste fönstret med angivet antal timmar, wrappar över midnatt om det behövs
    public List<ElpriserAPI.Elpris> findCheapestWindow(List<ElpriserAPI.Elpris> collectedPrices, int chargingHours) {
        List<ElpriserAPI.Elpris> cheapestWindow = new ArrayList<>();
        if (collectedPrices.isEmpty() || chargingHours <= 0 || collectedPrices.size() < chargingHours) { return cheapestWindow; }

        int totalPrices = collectedPrices.size();
        double minSum = 0;
        for (int i = 0; i < chargingHours; i++) {
            minSum += collectedPrices.get(i).sekPerKWh();
        }
        int startIndex = 0;
        for (int start = 1; start < totalPrices; start++) {
            double sum = 0;
            for (int i = 0; i < chargingHours; i++) {
                int index = (start + i) % totalPrices;
                sum += collectedPrices.get(index).sekPerKWh();
            }
            if (sum < minSum) {
                minSum = sum;
                startIndex = start;
            }
        }
        for (int i = 0; i < chargingHours; i++) {
            int index = (startIndex + i) % totalPrices;
            cheapestWindow.add(collectedPrices.get(index));
        }
        return cheapestWindow;
    }
}