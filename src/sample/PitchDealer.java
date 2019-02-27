package sample;

import java.util.ArrayList;
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

                Card card = new Card(j,face);
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

        for(int i = 0; i < 6; i++) {
            //loop until valid card is chosen
            Card addition = null;
            boolean validCardChosen = false;
            while(!validCardChosen) {
                char face;
                switch( rand.nextInt() % 4 ) {
                    case 0: face = 'C'; break;
                    case 1: face = 'D'; break;
                    case 2: face = 'H'; break;
                    case 3: face = 'S'; break;
                    default: face = 'C'; break;
                }

                int rank = rand.nextInt() % deck.getCards().size();

                addition = new Card(rank,face);

                if(deck.removeCard(addition)) {
                    validCardChosen = true;
                }

            }
            returnHand.add(addition);
        }
        return returnHand;
    }
}
