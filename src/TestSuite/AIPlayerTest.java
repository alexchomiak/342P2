package TestSuite;

import game.Card;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.AIPlayer;
import game.Deck;
import game.Pitch;

import static org.junit.jupiter.api.Assertions.*;

class AIPlayerTest {
    AIPlayer tester;
    Pitch testGame;

    @BeforeAll
    static void initializeTest() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @BeforeEach
    void initAIPlayer(){
        testGame = new Pitch(null,4,0,0);
        tester = new AIPlayer(new Deck(null), new Deck(null),testGame);
        //give tester cards
    }

    void giveHand() {
        for(int i = 0; i < 6; i++) tester.giveCard(new Card(null,i+1,'S',false,false));
    }

    @Test
    void constructorTest() {
        assertAll("AIPlayer correctly constructed",
                () -> assertEquals(testGame,tester.getParent(),"Correct parent game set"),
                () -> assertEquals(0,tester.getHand().getCards().size(),"Hand starts off empty"),
                () -> assertEquals(0, tester.getHand().getCards().size(), "Won tricks start off empty"),
                () -> assertEquals(-1, tester.getCurrentBid(), "Default bid set properly"),
                () -> assertFalse(tester.getTurnStarted(),"Turn state set properly"),
                () -> assertFalse(tester.getBidded(), "Bid state set properly")
                );
    }

    @Test
    void displayReturnsFlowpane() {
        assertEquals(FlowPane.class, tester.display().getClass(),"AI Display pane returned");
    }


    @Test
    void makeBestMove() {
        giveHand();
        //tests if turn state is correctly updated after move made
        tester.makeBestMove();
        assertAll("Correct turn state updated after move, and move made correctly",
                () -> assertTrue(tester.getCompleted(), "Turn state updated correctly"),
                () -> assertEquals(5,tester.getHand().getCards().size(), "Correct move made")
                );
    }

    @Test
    void calculateBestBid() {
        //test that calculate best bid returns a valid bid value
        giveHand();
        int bid = tester.calculateBestBid();
        assertTrue(bid >= 0 && bid != 1, "calculate best bid returns integer bid value");
    }

    @Test
    void makeBid() {
        giveHand();
        tester.makeBid();
        assertAll("Test that bid value is set, and bit state set correctly",
                () -> assertTrue(tester.getCurrentBid() != -1, "Bid value set correctly"),
                () -> assertTrue(tester.getBidded(), "Bid state set correctly")
                );
    }
}