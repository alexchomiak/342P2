package TestSuite;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.AIPlayer;
import game.Deck;
import game.Pitch;

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
    }

    @Test
    void constructorTest() {

    }

    @Test
    void displayReturnsNull() {

    }


    @Test
    void makeBestMove() {
    }

    @Test
    void startTurn() {
    }

    @Test
    void makeBid() {
    }
}