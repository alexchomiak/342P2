package TestSuite;

import javafx.embed.swing.JFXPanel;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.Card;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    Card testCard;

    @BeforeAll
    static void initializeTest() {
        //initialize jfxpanel to allow testing of JavaFX elements without opening a window
        JFXPanel jfxPanel = new JFXPanel();
    }

    @BeforeEach
    void initCard() {
        testCard = new Card(null,1,'S',false,true);
    }



    @Test
    void testConstructor() {
        Card constructorTest = new Card(null,10,'D',false,true);
        assertAll("Card object properly created",
                () -> assertEquals(10,constructorTest.getRank(),"Correct Rank initialized"),
                () -> assertEquals('D',constructorTest.getFace(),"Correct Face initialized"),
                () -> assertNotEquals(null,constructorTest.View(),"View object initialized"),
                () -> assertEquals(Rectangle.class,constructorTest.View().getClass(),"Rectangle object initialized"),
                () -> assertEquals(0,constructorTest.getX(),"Correct X coordinate initialized"),
                () -> assertEquals(0, constructorTest.getY(),"Correct Y coordinate initialized"),
                () -> assertEquals( 1.0, constructorTest.getScale(), "Correct Card Scale initialized")
        );

    }

    @Test
    void getFace() {
        assertEquals('S',testCard.getFace(),"Test getter for card face");
    }

    @Test
    void setFace() {
        testCard.setFace('C');
        assertEquals('C',testCard.getFace(),"Test setter for card face");
    }

    @Test
    void getRank() {
        assertEquals(1,testCard.getRank(),"Test getter for card rank");
    }

    @org.junit.jupiter.api.Test
    void setRank() {
        testCard.setRank(2);
        assertEquals(2,testCard.getRank(),"Test setter for card rank");
    }

    @Test
    void view() {
        //asserts view returns a rectangle objec
        assertEquals(Rectangle.class,testCard.View().getClass(),"Rectangle object returned");
    }

    @Test
    void changeCard() {
        Rectangle oldRectangleObject = testCard.View();
        testCard.changeCard(10,'C');

        //tests if card information is updated correctly
        assertAll("Should update card information correctly",
                () -> assertEquals(10,testCard.getRank(),"Properly updates card rank"),
                () -> assertEquals('C',testCard.getFace(),"Properly updates card face"),
                () -> assertTrue(testCard.View() != oldRectangleObject,"Properly updates card image")
        );
    }

    @Test
    void getX() {
        assertEquals(0,testCard.getX(),"Test getter for X Coordinate of Card");
    }

    @Test
    void getY() {
        assertEquals(0,testCard.getY(),"Test getter for Y Coordinate of Card");
    }

    @Test
    void setX() {
        testCard.setX(10);
        assertEquals(10,testCard.getX(),"Properly updates X Coordinate of card");
    }

    @Test
    void setY() {
        testCard.setY(10);
        assertEquals(10,testCard.getY(),"Properly update Y Coordinate of card");
    }

    @Test
    void setScale() {
        testCard.setScale(2.0);
        assertEquals(2.0,testCard.getScale(),"Properly update Scale of card rectangle");
    }

    @Test
    void getScale() {
        assertEquals(1.0,testCard.getScale(),"Test getter for Scale of card ");
    }


}