package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PitchDealer implements Dealer {
    private Deck deck;


    PitchDealer() {
        deck = new Deck(null);
        initializeDeck();
    }

    void initializeDeck() {
        //add all cards to deck

        for(int i = 0; i < 4; i++) {
            for(int j = 1; j < 14; j++) {
                char face;
                switch(i) {
                    case 0: face = 'C'; break;
                    case 1: face = 'D'; break;
                    case 2: face = 'H'; break;
                    case 3: face = 'S'; break;
                    default: face = 'C'; break;
                }

                Card card = new Card(this.deck,j,face,false, false);
                deck.addCard(card);
            }
        }
    }

    void resetDeck() {
        deck.clearDeck();
        initializeDeck();
    }


    public ArrayList<Card> dealHand(){
        Random rand = new Random();
        ArrayList<Card> returnHand = new ArrayList<Card>();

        Collections.shuffle(deck.getCards());
        for(int i = 0; i < 6; i++) {
            //loop until valid card is chosen
            Card addition = deck.getCards().get(0);
            deck.removeCard(addition);
            returnHand.add(addition);
        }
        return returnHand;
    }

    public Deck getDeck() {return this.deck;}
}
