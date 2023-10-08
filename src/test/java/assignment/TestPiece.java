package assignment;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestPiece {
    TetrisPiece stick = new TetrisPiece(Piece.PieceType.STICK);
    TetrisPiece ldog = new TetrisPiece(Piece.PieceType.LEFT_DOG);
    TetrisPiece rdog = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
    TetrisPiece square = new TetrisPiece(Piece.PieceType.SQUARE);
    TetrisPiece lhook = new TetrisPiece(Piece.PieceType.LEFT_L);
    TetrisPiece rhook = new TetrisPiece(Piece.PieceType.RIGHT_L);
    TetrisPiece tblock = new TetrisPiece(Piece.PieceType.T);

    TetrisPiece defaults[] = {stick, ldog, rdog, square, lhook, rhook, tblock};

    // test to make sure the getType() functions are correct
    @Test
    public void testType() {
        assertEquals(stick.getType(), Piece.PieceType.STICK);
        assertEquals(ldog.getType(), Piece.PieceType.LEFT_DOG);
        assertEquals(rdog.getType(), Piece.PieceType.RIGHT_DOG);
        assertEquals(square.getType(), Piece.PieceType.SQUARE);
        assertEquals(lhook.getType(), Piece.PieceType.LEFT_L);
        assertEquals(rhook.getType(), Piece.PieceType.RIGHT_L);
        assertEquals(tblock.getType(), Piece.PieceType.T);
    }

    // we can use a loop from here on out because we know getType() is correct
    @Test
    public void testBody() {
        for(TetrisPiece defaultType : defaults){
            assertEquals(defaultType.getBody(), defaultType.getType().getSpawnBody());
        }
    }

    @Test
    public void testGetHeight() {
        for(TetrisPiece defaultType : defaults){
            assertEquals(defaultType.getHeight(), defaultType.getType().getBoundingBox().getHeight());
        }
    }

    @Test
    public void testGetWidth() {
        for(TetrisPiece defaultType : defaults){
            assertEquals(defaultType.getWidth(), defaultType.getType().getBoundingBox().getWidth());
        }
    }

    // rotates a piece and checks its rotatation index. At the end, it checks to see that the points are the...
    // same as the spawn body. 
    @Test
    public void testRotationIndexAndRotate() {
        for(TetrisPiece defaultPiece : defaults){
            TetrisPiece rotatingPiece = new TetrisPiece(defaultPiece.getType());
            rotatingPiece.counterclockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 3);
            rotatingPiece.clockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 0);
            rotatingPiece.clockwisePiece();
            rotatingPiece.clockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 2);
            rotatingPiece.counterclockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 1);
            rotatingPiece.clockwisePiece();
            rotatingPiece.clockwisePiece();
            rotatingPiece.clockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 0);
            rotatingPiece.counterclockwisePiece();
            rotatingPiece.counterclockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 2);
            rotatingPiece.counterclockwisePiece();
            rotatingPiece.counterclockwisePiece();
            assertEquals(rotatingPiece.getRotationIndex(), 0);
            for(int i = 0; i < rotatingPiece.getBody().length; i++){
                assertEquals(rotatingPiece.getBody()[i], defaultPiece.getType().getSpawnBody()[i]);
            }
        }
    }

    // directly check to see if the values in the skirt match what's expected for all blocks
    // this function tests clockwise and counter clockwise rotations, as well as our skirt calculations
    @Test
    public void testSkirtAndRotate() {
        int expected_skirts[][][] = {
            { // expected values for stick skirts
                {2, 2, 2, 2},
                {Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE},
                {1, 1, 1, 1},
                {Integer.MAX_VALUE, 0, Integer.MAX_VALUE, Integer.MAX_VALUE}
            }, 
            { // expected values for ldog
                {2, 1, 1},
                {Integer.MAX_VALUE, 0, 1},
                {1, 0, 0},
                {0, 1, Integer.MAX_VALUE}
            }, 
            { // expected values for rdog
                {1, 1, 2},
                {Integer.MAX_VALUE, 1, 0},
                {0, 0, 1},
                {1, 0, Integer.MAX_VALUE}
            }, 
            { // expected values for square
                {0, 0},
                {0, 0},
                {0, 0},
                {0, 0},
            }, 
            { // expected values for lhook
                {1, 1, 1},
                {Integer.MAX_VALUE, 0, 2},
                {1, 1, 0},
                {0, 0, Integer.MAX_VALUE}
            },
            { // expected values for rhook
                {1, 1, 1},
                {Integer.MAX_VALUE, 0, 0},
                {0, 1, 1},
                {2, 0, Integer.MAX_VALUE}
            },
            { // expected values for T-block
                {1, 1, 1},
                {Integer.MAX_VALUE, 0, 1},
                {1, 0 , 1},
                {1, 0, Integer.MAX_VALUE}
            }
        };
        for(int i = 0; i < 7; i++){ // loop through piece types
            TetrisPiece testPiece = new TetrisPiece(defaults[i].getType());
            for(int j = 0; j < 4; j++){ // loop through rotations
                int testPieceSkirt[] = testPiece.getSkirt();
                for(int k = 0; k < testPiece.getWidth(); k++){ // loop through columns in skirt
                    assertEquals(testPieceSkirt[k], expected_skirts[i][j][k]);
                }
                // we use multiple turning methods to make sure they're both functioning correctly.
                testPiece.clockwisePiece();
                testPiece.counterclockwisePiece();
                testPiece.clockwisePiece();
            }
        }
    }

    @Test
    public void testEquals(){
        TetrisPiece tp1 = new TetrisPiece(Piece.PieceType.STICK);
        TetrisPiece tp2 = new TetrisPiece(Piece.PieceType.STICK);
        TetrisPiece tp3 = new TetrisPiece(Piece.PieceType.T);

        tp1.clockwisePiece(); tp1.clockwisePiece(); 
        tp2.counterclockwisePiece(); tp2.counterclockwisePiece();
        tp3.clockwisePiece(); tp3.clockwisePiece();

        assertEquals(tp1, tp2);
        assertNotEquals(tp1, defaults);
        assertNotEquals(tp3, tp1);
    }
}