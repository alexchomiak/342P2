package TestSuite;

import game.Card;
import game.Pitch;
import game.PitchDealer;
import game.Player;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitchTest {
    private Pitch testGame;


    @BeforeEach
    void initializeTest() {
        testGame = new Pitch(null,4,0,0);
    }


    @Test
    void createDealer() {
        PitchDealer test = testGame.createDealer();
        assertAll("Test proper dealer was created",
                () -> assertEquals(PitchDealer.class, test.getClass(), "Correct type of dealer initialized"),
                () -> assertEquals(52,test.getDeck().getCards().size(),"Initialized deck correct size")
                );
    }

    @Test
    void resetGameField() {
        for(int i = 1; i < 11; i++) {
            testGame.getGameField().addCard(new Card(null,i,'S',false,false));
        }

        testGame.resetGameField();
       assertEquals(0, testGame.getGameField().getCards().size(),"Gamefield reset, and Deck empty");
    }


    @Test
    void resetPlayers() {
        Player iterator = testGame.getPlayer();
        for(int i = 0; i < testGame.getPlayerCount(); i++) {
            for(int j = 1; j < 11; j++) {
                iterator.giveCard(new Card(null,j,'S',false,false));
                iterator.addWonCard(new Card(null,j,'S',false,false));
            }
            iterator = iterator.getNextPlayer();
        }
        testGame.resetPlayers(true);
        assertAll("Players correctly reset",
                () -> {
                    Player tester = testGame.getPlayer();
                    for(int i = 0; i < testGame.getPlayerCount(); i++) {
                        assertEquals(0, tester.getHand().getCards().size(),"Hand correctly reset");
                    }
                },
                () -> {
                    Player tester = testGame.getPlayer();
                    for(int i = 0; i < testGame.getPlayerCount(); i++) {
                        assertEquals(0, tester.getTricks().getCards().size(),"Tricks correctly reset");
                    }
                }
                );
    }

    @Test
    void resetPlayerBids() {
        Player iterator = testGame.getPlayer();
        for(int i = 0; i < testGame.getPlayerCount(); i++) {
            iterator.handleBid(2);
            iterator = iterator.getNextPlayer();
        }

        testGame.resetPlayerBids();
        assertAll("Player bids and bid state correctly reset",
                () -> {
                    Player tester = testGame.getPlayer();
                    for(int i = 0; i < testGame.getPlayerCount(); i++) {
                        assertFalse(tester.getBidded(), "Bid state correctly reset");
                    }
                },
                () -> {
                    Player tester = testGame.getPlayer();
                    for(int i = 0; i < testGame.getPlayerCount(); i++) {
                        assertEquals(-1,tester.getCurrentBid(),"Bid correctly reset");
                    }
                }
        );
    }

    @Test
    void dealPlayers() {
        testGame.dealPlayers();
        assertAll("All players have a deck size of 6",
                () -> {
                    Player iterator = testGame.getPlayer();
                    for(int i = 0; i < testGame.getPlayerCount(); i++) {
                        assertEquals(6,iterator.getHand().getCards().size(), "Handed card correct size");
                    }
                }
                );
    }

}