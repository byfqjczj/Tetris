package assignment;

import java.security.cert.CertPathValidatorException.BasicReason;
import java.util.*;

import assignment.Board.Result;

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, because we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class EpicBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;
    private ArrayList<Integer> rotationIndexes;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        
        if(currentBoard.getCurrentPiece() == null || currentBoard == null){
            return Board.Action.NOTHING;
        }

        for(int i = 0; i < 4; i++){
            if(currentBoard.getCurrentPiece() == null || currentBoard == null){
                return Board.Action.NOTHING;
            }
            TetrisBoard t = (TetrisBoard) currentBoard.testMove(Board.Action.NOTHING);
            enumerateOptions(t, i);
            currentBoard = currentBoard.testMove(Board.Action.CLOCKWISE);
        }
        

        int best = 0;
        int bestIndex = 0;
        int oldBoardHeights[] = new int[currentBoard.getWidth()];
        for(int i = 0; i < oldBoardHeights.length; i++){
            oldBoardHeights[i] = currentBoard.getColumnHeight(i); 
        }

        // Check all of the options and get the one with the highest score
        int lowestohs = Integer.MAX_VALUE;
        for (int i = 0; i < options.size(); i++) {
            int score = scoreBoard(options.get(i), oldBoardHeights);
            int ohs = overhangs(options.get(i), oldBoardHeights);
            if(ohs < lowestohs){
                if ((score > best) || (score == best && firstMoves.get(i) == Board.Action.DROP)) {
                    best = score;
                    bestIndex = i;
                }
            }
            
        }
        
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard, int rot) { // current issue: blocks are not being rotated to the correct position before they drop.
        Board.Action nextAct = Board.Action.DROP;
        Board.Action left_act = Board.Action.LEFT;
        Board.Action right_act = Board.Action.RIGHT;
        Board.Action clockwise = Board.Action.CLOCKWISE;
        Board.Action counterclockwise = Board.Action.COUNTERCLOCKWISE;

        if(rot == 1 || rot == 2){
            nextAct = clockwise;
            left_act = clockwise;
            right_act = clockwise;
        }
        if(rot == 3){
            nextAct = counterclockwise;
            left_act = counterclockwise;
            right_act = counterclockwise;
        }
        // We can always drop our current Piece
        if(currentBoard.getCurrentPiece() == null || currentBoard == null){
            return;
        }
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(nextAct); 
        //rotationIndexes.add(rot);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(left_act); 
            //rotationIndexes.add(rot);
            left.move(Board.Action.LEFT);
        }
        //firstMoves.set(index - 1, nextAct);

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(right_act); 
            //rotationIndexes.add(rot);
            right.move(Board.Action.RIGHT);
        }
        //firstMoves.set(index - 1, nextAct);
        //System.out.println(rotationIndexes.size() + " <r , fm> " + firstMoves.size());
    }

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */
    private int scoreBoard(Board newBoard, int[] oldBoardHeights) {
        return 100 - newBoard.getMaxHeight();
        /*int sum = 0;
        int newBoardHeights[] = new int[newBoard.getWidth()];
        for(int i = 0; i < newBoard.getWidth(); i++){
            newBoardHeights[i] = newBoard.getColumnHeight(i);
            sum += newBoardHeights[i];
        }
        return Integer.MAX_VALUE - sum;*/
    }

    private int overhangs(Board newBoard, int[] oldBoardHeights) {
        int ohs = 0;
        int newBoardHeights[] = new int[newBoard.getWidth()];
        for(int i = 0; i < newBoard.getWidth(); i++){
            newBoardHeights[i] = newBoard.getColumnHeight(i);
            if(newBoardHeights[i] > newBoard.getHeight()){
               // System.out.println("wut");
                ohs = Integer.MAX_VALUE;
                break;
            }
           // System.out.print("N: " + newBoardHeights[i] + " C: " + oldBoardHeights[i]);
            if(newBoardHeights[i] > oldBoardHeights[i]){
                //System.out.println("diff");
                for(int j = 0; j <= newBoardHeights[i]; j++){
                    if(newBoard.getGrid(i, j) == null){
                        ohs++;
                    }
                }
            }
        }
        //System.out.println(ohs);
        return ohs;
    }

}
