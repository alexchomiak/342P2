package TestSuite;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.Card;
import game.Deck;
import game.Pitch;
import game.Player;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player tester;
    Pitch testGame;

    @BeforeAll
    static void initializeTest() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @BeforeEach
    void initPlayer() {
        //initialize player before each test
        testGame = new Pitch(null,4,null);
        tester = testGame.getStartPlayer();
    }

    @Test
    void giveCard() {
        //test giveCard function
        Card c = new Card(null,3,'S',false);
        tester.giveCard(c);

        //assert that player recieves appropriate card
        assertAll("Player hand properly updated",
                () -> assertEquals(1,tester.getHand().getCards().size(), "Hand size is properly updated"),
                () -> assertEquals('S',tester.getHand().getCards().get(0).getFace(),"Proper card added (Face correct)"),
                () -> assertEquals(3,tester.getHand().getCards().get(0).getRank(),"Proper card added (Rank correct)")
                );
    }

    @Test
    void reset() {
        //test reset function
        tester.reset();

        //assert that turn state of player is properly reset
        assertAll("Player turn-state booleans properly reset",
                () -> assertEquals(false,tester.getCompleted(),"Turn completed false"),
                () -> assertEquals(false, tester.getTurnStarted(), "Turn started false")
                );
    }

    @Test
    void resetBids() {
        //test resetBids function
        tester.resetBids();

        //assert that resetBids updates bid state properly
        assertAll("Player bid-state booleans and bid amount properly reset",
                () -> assertEquals(-1, tester.getCurrentBid(),"Bid reset to -1"),
                () -> assertEquals(false,tester.getBidded(), "madeBid set to false")
        );
    }

    @Test
    void startTurn() {
        //test starTurn function
        tester.startTurn(true);
        assertEquals(true,tester.getTurnStarted(),"Tests if turn started correctly.");
    }

    @Test
    void addWonCard() {
        //test addWonCard function
        Card c = new Card(null,3,'S',false);
        //give player won card
        tester.addWonCard(c);

        //assert that they recieved correct card
        assertAll("Won card added to Players Trick Deck, information properly updated",
                () -> assertEquals(1,tester.getTricks().getCards().size(), "Size of tricks correctly updated"),
                () -> assertEquals(c.getRank(),tester.getTricks().getCards().get(0).getRank(),"Correct card added (Rank correct)"),
                () -> assertEquals(c.getFace(),tester.getTricks().getCards().get(0).getFace()," Correct card added (Face correct)")
                );
    }

    @Test
    void handleBid() {
        //test handle bid
        tester.handleBid(2);

        //assert bid state and amount set correctly
        assertAll("Handle bid updates player correctly",
                () -> assertEquals(2,tester.getCurrentBid(),"Bid value updated correctly"),
                () -> assertTrue(tester.getBidded(),"Bidded bool updated correctly")
        );
    }

    @Test
    void getWonTricks() {
        //test getWonTricks

        //give player 10 won cards
        for(int i = 1; i < 11; i++) {
            Card c = new Card(null,i,'S',false);
            tester.addWonCard(c);
        }

        //assert that they got those same 10 cards
        assertAll("Test getter for won tricks (correct deck returned)",
                () -> assertEquals(10, tester.getTricks().getCards().size(), "Correct size of returned deck"),
                () -> {
                    //loop through each card and make sure it is correct
                    for(int i = 0; i < 10; i++) {
                        Card c = tester.getTricks().getCards().get(i);
                        assertTrue(c.getRank() == i + 1 && c.getFace() == 'S', "Correct card at index " + Integer.toString(i));
                    }
                }
                );
    }

    @Test
    void getHand() {
        //test get hand

        //give player 6 cards
        for(int i = 1; i < 7; i++) {
            Card c = new Card(null,i,'S',false);
            tester.giveCard(c);
        }

        //assert that they recieved those same 6 cards
        assertAll("Test getter for hand (correct deck returned)",
                () -> assertEquals(6,tester.getHand().getCards().size(),"Correct size of returned deck"),
                () -> {
                    for(int i = 0; i < 6; i++) {
                        //loop through each card and make sure it is correct
                        Card c = tester.getHand().getCards().get(i);
                        assertTrue(c.getRank() == i + 1 && c.getFace() == 'S', "Correct card at index " + Integer.toString(i));
                    }
                }
        );
    }

    @Test
    void display() {
        //test display
        //assert that it returns a flowpane
        assertEquals(FlowPane.class,tester.display().getClass(),"display returns flowpane");
    }
}