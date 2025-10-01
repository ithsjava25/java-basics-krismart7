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

    public List<Double> hourlyMeanPrice(List<ElpriserAPI.Elpris> prices, List<Integer> meanHours) {
        List<Double> meanPrices = new ArrayList<>();
        if (prices.isEmpty()) return meanPrices;

        int currentHour = prices.get(0).timeStart().getHour();
        List<ElpriserAPI.Elpris> pricesInHour = new ArrayList<>();

        for (ElpriserAPI.Elpris price : prices) {
            int priceHour = price.timeStart().getHour();

            // Spara medelpris för föregående timme
            if (priceHour != currentHour) {
                meanHours.add(currentHour);
                double mean = meanPrice(pricesInHour);
                meanPrices.add(mean);

                // Skapa ny lista för nästa timme
                pricesInHour = new ArrayList<>();
                currentHour = priceHour;
            }
            pricesInHour.add(price);
        }
        // Lägg till sista timmens medelpris
        meanHours.add(currentHour);
        double mean = meanPrice(pricesInHour);
        meanPrices.add(mean);

        return meanPrices;
    }

    public void sortDescending(List<Integer> meanHours, List<Double> meanPrices) {
        for (int i = 0; i < meanPrices.size() - 1; i++) {
            for (int j = 0; j < meanPrices.size() - 1 - i; j++) {
                if (meanPrices.get(j) < meanPrices.get(j + 1)) {

                    //Byt plats på timme
                    double tempPrice = meanPrices.get(j);
                    meanPrices.set(j, meanPrices.get(j + 1));
                    meanPrices.set(j + 1, tempPrice);

                    // Byt plats på motsvarande timme
                    int tempHour = meanHours.get(j);
                    meanHours.set(j, meanHours.get(j + 1));
                    meanHours.set(j + 1, tempHour);
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