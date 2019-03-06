package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PitchDealer implements Dealer {
    private Deck deck;

    public PitchDealer() {
        deck = new Deck(null);
        initializeDeck();
    }

    public void initializeDeck() {
        //add all cards to deck
        //initialize deck
        //loop through all suits
        for(int i = 0; i < 4; i++) {

            //add every rank of each suit to the deck
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

    public void resetDeck() {
        //clear the deck
        deck.clearDeck();

        //reinitialize the deck
        initializeDeck();
    }



    public ArrayList<Card> dealHand(){
        //initialize returnHand
        ArrayList<Card> returnHand = new ArrayList<Card>();

        //shuffle the deck
        Collections.shuffle(deck.getCards());

        //give 6 cards from the deck to the player
        for(int i = 0; i < 6; i++) {
            //grab card from deck
            Card addition = deck.getCards().get(0);

            //remove the selected card
            deck.removeCard(addition);

            //add selected card to returnhand
            returnHand.add(addition);
        }
        return returnHand;
    }

    public Deck getDeck() {
        return this.deck;
    }
}
