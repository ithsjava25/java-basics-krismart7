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

    public List<Double> hourlyMeanPrice(List<ElpriserAPI.Elpris> prices, List<Integer> hoursOfDay) {
        List<Double> hourlyMeanPrices = new ArrayList<>();
        if (prices.isEmpty()) return hourlyMeanPrices;

        int currentHour = prices.get(0).timeStart().getHour();
        List<ElpriserAPI.Elpris> pricesInHour = new ArrayList<>();

        for (ElpriserAPI.Elpris pris : prices) {
            int hourlyPrice = pris.timeStart().getHour();

            if (hourlyPrice != currentHour) {
                hoursOfDay.add(currentHour);

                double mean = meanPrice(pricesInHour);
                hourlyMeanPrices.add(mean);

                pricesInHour = new ArrayList<>();
                currentHour = hourlyPrice;
            }
            pricesInHour.add(pris);
        }
        hoursOfDay.add(currentHour);
        double mean = meanPrice(pricesInHour);
        hourlyMeanPrices.add(mean);
        return hourlyMeanPrices;
    }

    public void sortDescending(List<Integer> hoursOfDay, List<Double> hourlyMeanPrice) {
        for (int i = 0; i < hourlyMeanPrice.size() - 1; i++) {
            for (int j = 0; j < hourlyMeanPrice.size() - 1 - i; j++) {

                if (hourlyMeanPrice.get(j) < hourlyMeanPrice.get(j + 1)) {

                    double tempPrice = hourlyMeanPrice.get(j);
                    hourlyMeanPrice.set(j, hourlyMeanPrice.get(j + 1));
                    hourlyMeanPrice.set(j + 1, tempPrice);

                    int tempHour = hoursOfDay.get(j);
                    hoursOfDay.set(j, hoursOfDay.get(j + 1));
                    hoursOfDay.set(j + 1, tempHour);
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