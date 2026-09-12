package com.ga.acme.controllers;

import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.models.Mastercard;
import com.ga.acme.models.MastercardPlatinum;
import com.ga.acme.models.MastercardTitanium;
import com.ga.acme.util.FileHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class Card {
    protected static ICard addCard(Boolean flag, String accountId, CardType cardType) {
        if (!flag) {
            return null;
        }
        ICard card = switch (cardType) {
            case MASTERCARD ->
                    new Mastercard(accountId);
            case MASTERCARDPLATINUM ->
                    new MastercardPlatinum(accountId);
            case MASTERCARDTITANIUM ->
                    new MastercardTitanium(accountId);
        };
        FileHandler.writeToFile(FilePath.CARDS.getPath(), card.toString());
        return card;
    }

    protected static ICard getCardById(String id) {
        ICard card = null;
        HashMap<String, Map<String, String>> cards = getDataFromFile(FilePath.CARDS.getPath());
        try {
            if (cards.containsKey(id)) {
                Map<String, String> values = cards.get(id);
                String type = values.get("type");
                String accountId = values.get("accountId");

                if (type.equalsIgnoreCase(CardType.MASTERCARD.toString())) {
                    card = new Mastercard(accountId);
                } else if (type.equalsIgnoreCase(CardType.MASTERCARDPLATINUM.toString())) {
                    card = new MastercardPlatinum(accountId);
                } else if (type.equalsIgnoreCase(CardType.MASTERCARDTITANIUM.toString())) {
                    card = new MastercardTitanium(accountId);
                } else {
                    throw new RecordNotFoundException("Unknown card type for card id " + id);
                }

                card.setId(id);
                card.setAccountId(values.get("accountId"));
                card.setDailyWithdrawn(Double.parseDouble(values.get("dailyWithdrawn")));
                card.setDailyDeposited(Double.parseDouble(values.get("dailyDeposited")));
                card.setDailyTransferred(Double.parseDouble(values.get("dailyTransferred")));
                String lastTransactionDate = values.get("lastTransactionDate");
                if (!lastTransactionDate.isEmpty() && !lastTransactionDate.equals("null")) {
                    card.setLastTransactionDate(LocalDate.parse(lastTransactionDate));
                } else {
                    card.setLastTransactionDate(null);
                }
            } else {
                throw new RecordNotFoundException("Card with id " + id + " not found");
            }
        } catch (
                RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return card;
    }

    protected static ICard getCardByTypeForAccount(IAccount account, CardType cardType) {
        return switch (cardType) {
            case MASTERCARD ->
                    account.getMastercard();
            case MASTERCARDPLATINUM ->
                    account.getMastercardPlatinum();
            case MASTERCARDTITANIUM ->
                    account.getMastercardTitanium();
        };
    }
}