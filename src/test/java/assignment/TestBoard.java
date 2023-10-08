package assignment;

import java.awt.*;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class TestBoard {
    private Piece.PieceType stick = Piece.PieceType.STICK;

    /*
     * Test width and height methods
     */
    @Test
    public void getWidthAndHeight(){
        TetrisBoard board = new TetrisBoard(20, 20);
        assertEquals(board.getWidth(), 20);
        assertEquals(board.getHeight(), 20);
    }

    /*
     * Test currentpiece, currentposition, and nextpiece
     */
    @Test
    public void testCurrentPieceAndPositionAndNextPiece(){
        TetrisBoard board = new TetrisBoard(10, 24);
        Point startPos = new Point(5, 20);
        board.nextPiece(new TetrisPiece(stick), startPos);
        assertEquals(board.getCurrentPiece().getType(), stick);
        board.move(Board.Action.DOWN);
        assertEquals(board.getCurrentPiecePosition().getY(), 19);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        assertEquals(board.getCurrentPiecePosition().getY(), 17);
        board.move(Board.Action.LEFT);
        assertEquals(board.getCurrentPiecePosition().getX(), 4);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        assertEquals(board.getCurrentPiecePosition().getX(), 1);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        assertEquals(board.getCurrentPiecePosition().getX(), 0);
        for(int i = 0; i < 20; i++){
            board.move(Board.Action.RIGHT);
        }
        assertEquals(board.getCurrentPiecePosition().getX(), board.getWidth() - board.getCurrentPiece().getWidth());

        TetrisPiece piece = new TetrisPiece(stick);
        Point illegalPos1 = new Point(-1, -1);
        Point illegalPos2 = new Point(5, board.getHeight());
        boolean caught = false;
        try{
            board.nextPiece(piece, illegalPos1);
        }
        catch(IllegalArgumentException illegal_expression){
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        try{
            board.nextPiece(piece, illegalPos2);
        }
        catch(IllegalArgumentException illegal_expression){
            caught = true;
        }
        assertTrue(caught);
        caught = false;
        board.nextPiece(piece, startPos);
        try{
            board.nextPiece(piece, startPos);
        }
        catch(IllegalArgumentException illegal_expression){
            caught = true;
        }
        assertTrue(caught);

    }

    // places a few pieces, checks the pieces, counts null pieces
    @Test 
    public void getGrid(){
        TetrisBoard board = new TetrisBoard(10, 24);
        board.nextPiece(new TetrisPiece(stick), new Point(5, 10));
        assertEquals(board.getGrid(5, 12), stick);
        assertEquals(board.getGrid(0, 0), null);
        // ensure only four tiles are filled
        int notNullPiece = 0;
        for(int i = 0; i < board.getWidth(); i++){
            for(int j = 0; j < board.getHeight(); j++){
                if(board.getGrid(i, j) != null){
                    notNullPiece++;
                }
            }
        }
        assertEquals(notNullPiece, 4);

        board.move(Board.Action.CLOCKWISE);
        notNullPiece = 0;
        for(int i = 0; i < board.getWidth(); i++){
            for(int j = 0; j < board.getHeight(); j++){
                if(board.getGrid(i, j) != null){
                    notNullPiece++;
                }
            }
        }
        assertEquals(notNullPiece, 4);
    }

    // same grid, same piece, same grid
    @Test
    public void testEquals(){
        TetrisBoard t1 = new TetrisBoard(20, 10);
        TetrisBoard t2 = new TetrisBoard(10, 10);
        assertFalse(t1.equals(t2));
        assertTrue(t1.equals(t2) == t2.equals(t1));

        TetrisBoard t3 = new TetrisBoard(20, 10);
        t1.nextPiece(new TetrisPiece(stick), new Point(1,1));
        t3.nextPiece(new TetrisPiece(stick), new Point(1, 1));
        assertTrue(t1.equals(t3));
        t1.move(Board.Action.RIGHT);
        assertFalse(t1.equals(t3));
        t3.move(Board.Action.RIGHT);
        assertTrue(t1.equals(t3));

        t1.move(Board.Action.CLOCKWISE);
        t3.move(Board.Action.CLOCKWISE);
        assertTrue(t1.equals(t3));
        t1.move(Board.Action.DOWN);
        t3.move(Board.Action.DOWN);
        t1.nextPiece(new TetrisPiece(stick), new Point(5, 5));
        assertFalse(t1.equals(t3));

        assertTrue(t1.equals(t1));
    }

    @Test
    public void testTestMove(){
        Board.Action noAction = Board.Action.NOTHING;
        TetrisBoard t1 = new TetrisBoard(20, 10);
        TetrisBoard t2 = new TetrisBoard(20, 10); 
        assertTrue(t2.equals(t1.testMove(noAction)));

        TetrisBoard t1_left = (TetrisBoard) t1.testMove(Board.Action.LEFT);
        assertTrue(t1.equals(t1_left));

        t1_left.nextPiece(new TetrisPiece(stick), new Point(5, 5));
        assertFalse(t1.equals(t1_left));
        t1_left.move(Board.Action.LEFT);
        t1_left.move(Board.Action.DOWN);
        t1_left.move(Board.Action.DROP);
        assertEquals(t1_left.getLastAction(), Board.Action.DROP);
        assertEquals(t1_left.getLastResult(), Board.Result.PLACE);
    }

    @Test
    public void testingetLast(){
        TetrisBoard t1 = new TetrisBoard(20, 10);
        assertEquals(t1.getLastResult(), Board.Result.NO_PIECE);
        t1.nextPiece(new TetrisPiece(stick), new Point(5, 5));
        t1.move(Board.Action.LEFT);
        assertEquals(t1.getLastAction(), Board.Action.LEFT);
        assertEquals(t1.getLastResult(), Board.Result.SUCCESS);
        t1.move(Board.Action.LEFT);
        t1.move(Board.Action.LEFT);
        assertEquals(t1.getLastAction(), Board.Action.LEFT);
        assertEquals(t1.getLastResult(), Board.Result.SUCCESS);
        t1.move(Board.Action.LEFT);
        assertEquals(t1.getLastAction(), Board.Action.LEFT);
        assertEquals(t1.getLastResult(), Board.Result.SUCCESS);
        t1.move(Board.Action.LEFT);
        assertEquals(t1.getLastAction(), Board.Action.LEFT);
        assertEquals(t1.getLastResult(), Board.Result.SUCCESS);
        t1.move(Board.Action.LEFT);
        assertEquals(t1.getLastAction(), Board.Action.LEFT);
        assertEquals(t1.getLastResult(), Board.Result.OUT_BOUNDS);
        t1.move(Board.Action.DROP);
        assertEquals(t1.getLastAction(), Board.Action.DROP);
        assertEquals(t1.getLastResult(), Board.Result.PLACE);

        t1.move(Board.Action.CLOCKWISE);
        assertEquals(t1.getLastAction(), Board.Action.CLOCKWISE);
        assertEquals(t1.getLastResult(), Board.Result.NO_PIECE);
        t1.move(Board.Action.DROP);
        assertEquals(t1.getLastAction(), Board.Action.DROP);
    }

    @Test
    public void testRowsClear() {
        TetrisBoard board = new TetrisBoard(4, 10);
        board.nextPiece(new TetrisPiece(stick), new Point(0, 6));
        board.move(Board.Action.DROP);
        assertEquals(board.getRowsCleared(), 1);
        board.nextPiece(new TetrisPiece(stick), new Point(0, 6));
        board.move(Board.Action.DROP);
        assertEquals(board.getRowsCleared(), 2);
        assertEquals(board.getLastResult(), Board.Result.PLACE);

        board.nextPiece(new TetrisPiece(stick), new Point(0, 1));
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        assertEquals(board.getLastResult(), Board.Result.PLACE);
        assertEquals(board.getGrid(0, 2), null);
        assertEquals(board.getRowsCleared(), 3); 
        
    }

    @Test
    public void testRowWidthAndColumnHeight() {
        TetrisBoard board = new TetrisBoard(4, 10);
        board.nextPiece(new TetrisPiece(stick), new Point(0, 6));
        assertEquals(board.getRowWidth(6), 0);
        assertEquals(board.getColumnHeight(0), 0);
        assertEquals(board.getColumnHeight(1), 0);

        board.move(Board.Action.DROP);
        assertEquals(board.getRowWidth(0), 0);
        assertEquals(board.getColumnHeight(0), 0);
        assertEquals(board.getColumnHeight(1), 0);
        assertEquals(board.getColumnHeight(2), 0);
        assertEquals(board.getColumnHeight(3), 0);
        
        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_DOG), new Point(0, 5));
        board.move(Board.Action.DROP); 
        assertEquals(board.getRowWidth(0), 2);
        assertEquals(board.getRowWidth(0), 2);
        assertEquals(board.getColumnHeight(0), 2);
        assertEquals(board.getColumnHeight(1), 2);
        assertEquals(board.getColumnHeight(2), 1);
        assertEquals(board.getColumnHeight(3), 0);

        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_DOG), new Point(1, 5));
        board.move(Board.Action.DROP);
        assertEquals(board.getRowWidth(0), 2);
        assertEquals(board.getRowWidth(1), 2);
        assertEquals(board.getRowsCleared(), 2);
        assertEquals(board.getColumnHeight(0), 0);
        assertEquals(board.getColumnHeight(1), 2);
        assertEquals(board.getColumnHeight(2), 2);
        assertEquals(board.getColumnHeight(3), 0);

        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_DOG), new Point(1, 6));
        board.move(Board.Action.DROP);
        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_DOG), new Point(1, 6));
        board.move(Board.Action.DROP);
        assertEquals(board.getColumnHeight(0), 0);
        assertEquals(board.getColumnHeight(1), 6);
        assertEquals(board.getColumnHeight(2), 6);
        assertEquals(board.getColumnHeight(3), 5);
    }

    @Test
    public void testMaxHeightAndDropHeight() {
        TetrisBoard board = new TetrisBoard(4, 10);
        assertEquals(board.getMaxHeight(), 0);
        board.nextPiece(new TetrisPiece(stick), new Point(0, 6));
        assertEquals(board.dropHeight(new TetrisPiece(stick), 0), 0);
        board.move(Board.Action.DROP);
        board.nextPiece(new TetrisPiece(Piece.PieceType.LEFT_DOG), new Point(0, 6)); 
        board.move(Board.Action.DROP); 
        board.nextPiece(new TetrisPiece(Piece.PieceType.RIGHT_DOG), new Point(0, 6));
        board.move(Board.Action.CLOCKWISE);
        assertEquals(board.dropHeight(board.getCurrentPiece(), (int) board.getCurrentPiecePosition().getX()), 1);
        assertEquals(board.dropHeight(board.getCurrentPiece(), (int) board.getCurrentPiecePosition().getX() - 1), 2);
        board.move(Board.Action.DROP);
        assertEquals(board.getMaxHeight(), 4);
    }
}
