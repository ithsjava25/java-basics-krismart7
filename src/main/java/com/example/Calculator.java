package com.example;
import com.example.api.ElpriserAPI;
import java.util.ArrayList;
import java.util.List;

public class Calculator {

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

    public List<Double> hourlyMeanPrices(List<ElpriserAPI.Elpris> prices, List<Integer> hoursOfDay) {
        List<Double> hourlyMeans = new ArrayList<>();
        if (prices.isEmpty()) return hourlyMeans;

        int currentHour = prices.get(0).timeStart().getHour();
        List<ElpriserAPI.Elpris> pricesInHour = new ArrayList<>();

        for (ElpriserAPI.Elpris pris : prices) {
            int hour = pris.timeStart().getHour();
            if (hour != currentHour) {
                hoursOfDay.add(currentHour);
                double mean = meanPrice(pricesInHour);
                hourlyMeans.add(mean);
                pricesInHour = new ArrayList<>();
                currentHour = hour;
            }
            pricesInHour.add(pris);
        }
        hoursOfDay.add(currentHour);
        double mean = meanPrice(pricesInHour);
        hourlyMeans.add(mean);
        return hourlyMeans;
    }

    public void sortDescending(List<Integer> hours, List<Double> hourlyMeans) {
        for (int i = 0; i < hourlyMeans.size() - 1; i++) {
            for (int j = 0; j < hourlyMeans.size() - 1 - i; j++) {

                if (hourlyMeans.get(j) < hourlyMeans.get(j + 1)) {

                    double tempPrice = hourlyMeans.get(j);
                    hourlyMeans.set(j, hourlyMeans.get(j + 1));
                    hourlyMeans.set(j + 1, tempPrice);

                    int tempHour = hours.get(j);
                    hours.set(j, hours.get(j + 1));
                    hours.set(j + 1, tempHour);
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
        for (int i = 0; i < windowHours; i++) {
            minSum += prices.get(i).sekPerKWh();
        }
        int startIndex = 0;
        for (int start = 1; start < totalPrices; start++) {
            double sum = 0;
            for (int i = 0; i < windowHours; i++) {
                int index = (start + i) % totalPrices;
                sum += prices.get(index).sekPerKWh();
            }
            if (sum < minSum) {
                minSum = sum;
                startIndex = start;
            }
        }
        for (int i = 0; i < windowHours; i++) {
            int index = (startIndex + i) % totalPrices;
            cheapestWindow.add(prices.get(index));
        }
        return cheapestWindow;
    }
}