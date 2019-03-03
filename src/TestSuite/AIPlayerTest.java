package TestSuite;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.AIPlayer;
import sample.Deck;
import sample.Pitch;

import static org.junit.jupiter.api.Assertions.*;

class AIPlayerTest {
    AIPlayer tester;
    Pitch testGame;

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