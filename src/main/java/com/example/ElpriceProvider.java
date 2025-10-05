package com.example;
import com.example.api.ElpriserAPI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Ansvarar för att hämta elpriser från API och samla dem över flera dagar
public class ElpriceProvider {
    private final ElpriserAPI api;

    // Tar emot ett API-objekt för att kunna hämta priser
    public ElpriceProvider(ElpriserAPI api) {
        this.api = api;
    }

    // Hämtar priser för angivet datum och zon, inklusive nästa dag om tillgängligt
    public List<ElpriserAPI.Elpris> getCollectedPrices(LocalDate chosenDate, ElpriserAPI.Prisklass zoneEnum) {
        List<ElpriserAPI.Elpris> collectedPrices = new ArrayList<>();

        List<ElpriserAPI.Elpris> day1 = api.getPriser(chosenDate, zoneEnum);
        if (!day1.isEmpty()) collectedPrices.addAll(day1);

        List<ElpriserAPI.Elpris> day2 = api.getPriser(chosenDate.plusDays(1), zoneEnum);
        if (!day2.isEmpty()) collectedPrices.addAll(day2);

        return collectedPrices;
    }

    // Kontrollerar om listan med priser är tom och skriver ut meddelande om så är fallet
    public boolean noPrices(List<ElpriserAPI.Elpris> prices, LocalDate chosenDate, ElpriserAPI.Prisklass zoneEnum) {
        if (prices.isEmpty()) {
            System.out.println("Inga priser tillgängliga för " + chosenDate + " i " + zoneEnum);
            return true;
        }
        return false;
    }
}