package TestSuite;

import game.Card;
import game.Deck;
import game.PitchDealer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PitchDealerTest {
    PitchDealer testDealer;

    @BeforeEach
    void initializeTest() {
        testDealer = new PitchDealer();
    }

    @Test
    void getDeck() {
        //test getter for get deck
        assertEquals(Deck.class, testDealer.getDeck().getClass(), "Pitch Dealer getter should return a Deck of Cards");
    }


    @Test
    void constructorTest() {
        //test that initialized deck is correct size
        assertEquals(52,testDealer.getDeck().getCards().size(),"Initialized Deck correct size");
    }

    @Test
    void resetDeck() {
        //reset deck should clear deck, then re initialize the dealers deck (effectively reshuffling the deck)
        testDealer.resetDeck();
        assertEquals(52, testDealer.getDeck().getCards().size(), "Deck size should be 52");
    }

    @Test
    void dealHand() {
        ArrayList<Card> testHand = testDealer.dealHand();

        assertAll("Hand correctly dealt from deck",
                () ->  assertEquals(6, testHand.size(), "Dealed hand should be of size 6"),
                () -> assertEquals(46, testDealer.getDeck().getCards().size(), "Deck size should be 46 after 6 cards taken")

                );

    }

    @Test
    void dealHand4Times() {
        for(int i = 0; i < 4; i++) {
            assertEquals(6, testDealer.dealHand().size(), "Dealt hand should always equal 6");
        }
        //test that deck is correct size
        assertEquals(28,testDealer.getDeck().getCards().size(), "Deck should be 24 cards large");
    }

}
