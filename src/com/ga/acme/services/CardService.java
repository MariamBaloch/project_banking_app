package com.ga.acme.services;

import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.models.Card;
import com.ga.acme.models.Mastercard;
import com.ga.acme.models.MastercardPlatinum;
import com.ga.acme.models.MastercardTitanium;
import com.ga.acme.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class CardService {
    protected static ICard addCard(Boolean flag, String accountId, CardType cardType) {
        if (!flag) {
            return null;
        }
        ICard card = switch (cardType) {
            case MASTERCARD -> new Mastercard(accountId);
            case MASTERCARDPLATINUM -> new MastercardPlatinum(accountId);
            case MASTERCARDTITANIUM -> new MastercardTitanium(accountId);
        };
        FileHandler.writeToFile(FilePath.CARDS.getPath(), card.toString());
        return card;
    }

    public static ICard getCardById(String id) {
        ICard card = null;
        HashMap<String, Map<String, String>> cards = getDataFromFile(FilePath.CARDS.getPath());
        try {
            if (cards.containsKey(id)) {
                Map<String, String> values = cards.get(id);
                card = Card.mapToCardObject(values);
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
            case MASTERCARD -> account.getMastercard();
            case MASTERCARDPLATINUM -> account.getMastercardPlatinum();
            case MASTERCARDTITANIUM -> account.getMastercardTitanium();
        };
    }
}