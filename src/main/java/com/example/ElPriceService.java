package com.example;
import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ElPriceService {
    private final ElpriserAPI api;

    public ElPriceService(ElpriserAPI api) {
        this.api = api;
    }

    public List<ElpriserAPI.Elpris> getCollectedPrices(LocalDate chosenDate, ElpriserAPI.Prisklass zoneEnum) {
        List<ElpriserAPI.Elpris> collectedPrices = new ArrayList<>();

        List<ElpriserAPI.Elpris> pricesDay1 = api.getPriser(chosenDate, zoneEnum);
        if (!pricesDay1.isEmpty()) collectedPrices.addAll(pricesDay1);

        List<ElpriserAPI.Elpris> pricesDay2 = api.getPriser(chosenDate.plusDays(1), zoneEnum);
        if (!pricesDay2.isEmpty()) collectedPrices.addAll(pricesDay2);

        return collectedPrices;
    }

    public boolean noPrices(List<ElpriserAPI.Elpris> prices, ElpriserAPI.Prisklass zoneEnum, LocalDate chosenDate) {
        if (prices.isEmpty()) {
            System.out.println("Inga priser tillgängliga för " + chosenDate + " i " + zoneEnum);
            return true;
        }
        return false;
    }
}