package TestSuite;

import game.Card;
import game.Deck;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    FlowPane testPane;
    Deck testDeck;
    @BeforeAll
    static void initializeTests() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @BeforeEach
    void initializeTest() {
        testPane = new FlowPane();
        testDeck = new Deck(testPane);
    }

    @Test
    void addCard() {
        Card test = new Card(null,1,'S',false,false);
        testDeck.addCard(test);
        assertAll("Deck correctly added card",
                () -> assertEquals(1, testDeck.getCards().size(),"Deck size correct"),
                () -> assertEquals('S',testDeck.getCards().get(0).getFace(),"Correct card added (Face)"),
                () -> assertEquals(1,testDeck.getCards().get(0).getRank(),"Correct Card added (Rank)")
        );
    }

    @Test
    void removeCard() {
        Card test = new Card(null,1,'S',false,false);
        testDeck.addCard(test);
        testDeck.removeCard(test);
        assertEquals(0,testDeck.getCards().size(),"Correctly added and removed card from deck");
    }

    @Test
    void getDisplayPane() {
        assertEquals(testPane,testDeck.getDisplayPane(),"Returns correct display pane");
    }

    @Test
    void moveCardTo() {
        Card test = new Card(null,1,'S',false,false);
        Deck destination = new Deck(null);
        testDeck.addCard(test);
        testDeck.moveCardTo(destination,test);
        assertAll("Card correctly moved to destination deck",
                () -> assertEquals(0, testDeck.getCards().size(),"Test deck size correct size"),
                () -> assertEquals(1,destination.getCards().size(), "Destination deck correct size"),
                () -> assertEquals('S',destination.getCards().get(0).getFace(),"Correct card added to destination (Face)"),
                () -> assertEquals(1, destination.getCards().get(0).getRank(),"Correct card added to destination (Rank)")
                );
    }

    @Test
    void clearDeck() {
        for(int i = 1; i < 14; i++) {
            testDeck.addCard(new Card(null,i,'S',false,false));
        }
        testDeck.clearDeck();
        assertEquals(0,testDeck.getCards().size(),"Deck correctly cleared");
    }
}