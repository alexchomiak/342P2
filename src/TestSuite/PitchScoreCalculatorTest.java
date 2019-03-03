package TestSuite;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitchScoreCalculatorTest {

    @BeforeAll
    static void initializeTest() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @Test
    void calculateTrickWinner() {
    }

    @Test
    void calculateRoundScore() {
    }
}