package TestSuite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.Card;
import sample.Deck;
import sample.Pitch;
import sample.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player tester;
    Pitch testGame;

    @BeforeEach
    void initPlayer() {
        testGame = new Pitch(null,4,0,0);
        tester = new Player(new Deck(null), new Deck(null), testGame);
    }

    @Test
    void giveCard() {
        Card c = new Card(null,3,'S',false);
        tester.giveCard(c);
        assertAll("Player hand properly updated",
                () -> assertEquals(1,tester.getHand().getCards().size(), "Hand size is properly updated"),
                () -> assertEquals('S',tester.getHand().getCards().get(0).getFace(),"Proper card added (Face correct)"),
                () -> assertEquals(3,tester.getHand().getCards().get(0).getRank(),"Proper card added (Rank correct)")
                );
    }

    @Test
    void reset() {
        tester.reset();
        assertAll("Player turn-state booleans properly reset",
                () -> assertEquals(false,tester.getCompleted(),"Turn completed false"),
                () -> assertEquals(false, tester.getTurnStarted(), "Turn started false")
                );
    }

    @Test
    void resetBids() {
        tester.resetBids();
        assertAll("Player bid-state booleans and bid amount properly reset",
                () -> assertEquals(-1, tester.getCurrentBid(),"Bid reset to -1"),
                () -> assertEquals(false,tester.getBidded(), "madeBid set to false")
                );
    }

    @Test
    void startTurn() {
        tester.startTurn(true);
        assertEquals(true,tester.getTurnStarted(),"Tests if turn started correctly.");
    }

    @Test
    void addWonCard() {
        Card c = new Card(null,3,'S',false);
    }

    @Test
    void handleBid() {
    }

    @Test
    void getWonTricks() {
    }

    @Test
    void getHand() {
    }

    @Test
    void display() {
    }
}