package TestSuite;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @BeforeAll
    static void initializeTest() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @Test
    void addCard() {
    }

    @Test
    void removeCard() {
    }

    @Test
    void getDisplayPane() {
    }

    @Test
    void moveCardTo() {
    }

    @Test
    void getCards() {
    }

    @Test
    void setSelectedCard() {
    }

    @Test
    void getSelectedCard() {
    }

    @Test
    void setSelectable() {
    }

    @Test
    void setScale() {
    }

    @Test
    void getScale() {
    }

    @Test
    void setRenderable() {
    }

    @Test
    void clearDeck() {
    }
}