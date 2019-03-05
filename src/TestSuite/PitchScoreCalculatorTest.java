package TestSuite;

import game.*;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitchScoreCalculatorTest {
    PitchScoreCalculator testCalculator;
    Pitch testGame;
    Player player1;
    Player player2;
    Player player3;

    @BeforeAll
    static void initializeTests() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }


    @BeforeEach
    public  void initializeTest() {
        testGame = new Pitch(null,3,0,0);
        testCalculator = new PitchScoreCalculator();
        player1 = testGame.getStartPlayer();
        player2 = player1.getNextPlayer();
        player3 = player2.getNextPlayer();
    }

    @Test
    void calculateTrickWinnerTrumpShouldWin() {
        testGame.setCurrentTrumpSuit('S');

        //player 1 card
        testGame.getCurrentTrick().addCard(new Card(null,13,'C',false,false));
        //player 2 card
        testGame.getCurrentTrick().addCard(new Card(null,2,'S',false,false));
        //player 3 card
        testGame.getCurrentTrick().addCard(new Card(null,1,'D',false,false));

        assertEquals(player2,testCalculator.calculateTrickWinner(testGame));

    }
    @Test
    void calculateTrickWinnerLeadShouldWin() {
        testGame.setCurrentTrumpSuit('S');
        testGame.setCurrentLeadSuit('D');

        //player 1 card
        testGame.getCurrentTrick().addCard(new Card(null,13,'C',false,false));
        //player 2 card
        testGame.getCurrentTrick().addCard(new Card(null,2,'H',false,false));
        //player 3 card
        testGame.getCurrentTrick().addCard(new Card(null,1,'D',false,false));

        assertEquals(player3,testCalculator.calculateTrickWinner(testGame));
    }


    @Test
    void calculateTrickWinnerHighestRankTrump() {
        testGame.setCurrentTrumpSuit('S');
        //player 1 card
        testGame.getCurrentTrick().addCard(new Card(null,13,'S',false,false));
        //player 2 card
        testGame.getCurrentTrick().addCard(new Card(null,2,'S',false,false));
        //player 3 card
        testGame.getCurrentTrick().addCard(new Card(null,1,'S',false,false));

        assertEquals(player3,testCalculator.calculateTrickWinner(testGame));
    }

    @Test
    void calculateTrickWinnerHighestRankLead() {
        testGame.setCurrentTrumpSuit('S');
        testGame.setCurrentLeadSuit('D');

        //player 1 card
        testGame.getCurrentTrick().addCard(new Card(null,13,'D',false,false));
        //player 2 card
        testGame.getCurrentTrick().addCard(new Card(null,2,'D',false,false));
        //player 3 card
        testGame.getCurrentTrick().addCard(new Card(null,1,'D',false,false));

        assertEquals(player3,testCalculator.calculateTrickWinner(testGame));
    }


    @Test
    void calculateRoundScorePlayerBid() {
        testGame.setCurrentTrumpSuit('S');

        //make player 1 have a bid of 2
        player1.handleBid(2);

        //give player 1 all tricks won in the round (simulation)
        for(int i = 0; i < 3; i++) {
            char suit = 'E';
            switch(i){
                case 0: suit = 'S'; break;
                case 1: suit = 'D'; break;
                case 2: suit = 'H'; break;
            }
            player1.addWonCard(new Card(null,1,suit,false,false));
            player1.addWonCard(new Card(null,2,suit,false,false));
            player1.addWonCard(new Card(null,10,suit,false,false));
            player1.addWonCard(new Card(null,11,suit,false,false));
            player1.addWonCard(new Card(null,12,suit,false,false));
            player1.addWonCard(new Card(null,13,suit,false,false));

        }

        testCalculator.calculateRoundScore(testGame);
        assertEquals(4,testGame.getCurrentScores().get(0),"Score for player 1 calculated correctly (Bid 2) (Won all Tricks)");
    }

    @Test
    void calculateRoundScorePlayerDidNotBid() {
        testGame.setCurrentTrumpSuit('S');

        //make player 1 pass
        player1.handleBid(0);

        //give player 1 all tricks won in the round (simulation)
        for(int i = 0; i < 3; i++) {
            char suit = 'E';
            switch(i){
                case 0: suit = 'S'; break;
                case 1: suit = 'D'; break;
                case 2: suit = 'H'; break;
            }
            player1.addWonCard(new Card(null,1,suit,false,false));
            player1.addWonCard(new Card(null,2,suit,false,false));
            player1.addWonCard(new Card(null,10,suit,false,false));
            player1.addWonCard(new Card(null,11,suit,false,false));
            player1.addWonCard(new Card(null,12,suit,false,false));
            player1.addWonCard(new Card(null,13,suit,false,false));

        }

        testCalculator.calculateRoundScore(testGame);
        assertEquals(0,testGame.getCurrentScores().get(0),"Score for player 1 calculated correctly (Passed) (Won all Tricks)");

    }


    @Test
    void calculateRoundScorePlayerBidSmudge() {
        testGame.setCurrentTrumpSuit('S');

        //make player 1 have a bid of 5 (Smudge)
        player1.handleBid(5);

        //give player 1 all tricks won in the round (simulation)
        for(int i = 0; i < 3; i++) {
            char suit = 'E';
            switch(i){
                case 0: suit = 'S'; break;
                case 1: suit = 'D'; break;
                case 2: suit = 'H'; break;
            }
            player1.addWonCard(new Card(null,1,suit,false,false));
            player1.addWonCard(new Card(null,2,suit,false,false));
            player1.addWonCard(new Card(null,10,suit,false,false));
            player1.addWonCard(new Card(null,11,suit,false,false));
            player1.addWonCard(new Card(null,12,suit,false,false));
            player1.addWonCard(new Card(null,13,suit,false,false));

        }

        testCalculator.calculateRoundScore(testGame);
        assertEquals(5,testGame.getCurrentScores().get(0),"Score for player 1 calculated correctly (Bid 5) (Won all Tricks)");

    }

}