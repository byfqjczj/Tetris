package assignment;

import java.util.*;

/**
 * A Lame Brain implementation for JTetris; tries all possible places to put the
 * piece (but ignoring rotations, because we're lame), trying to minimize the
 * total height of pieces on the board.
 */
public class BestBrain implements Brain {

    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;

    /**
     * Decide what the next move should be based on the state of the board.
     */
    public Board.Action nextMove(Board currentBoard) {
        // Fill the our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        int best = Integer.MIN_VALUE;
        int bestohs = Integer.MAX_VALUE;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            Board option = options.get(i);
            int score = scoreBoard(option);
            if((ohs(option) == bestohs || 
                        option.getMaxHeight() > option.getHeight() - 10) && 
                        score < best) {
                bestohs = ohs(option);
                best = score;
                bestIndex = i;
            }
            else if(ohs(option) < bestohs){
                best = score;
                bestIndex = i;
                bestohs = ohs(options.get(i));
            }
        }

        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    /**
     * Test all of the places we can put the current Piece.
     * Since this is just a Lame Brain, we aren't going to do smart
     * things like rotating pieces.
     */
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);
        Board rot1 = currentBoard.testMove(Board.Action.CLOCKWISE);
        int ct = 0;
        while(rot1.getLastResult() == Board.Result.SUCCESS)
        {
            options.add(rot1.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.CLOCKWISE);
            rot1.move(Board.Action.CLOCKWISE);
            ct++;
            if(ct>=2)
            {
                break;
            }
        }
        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            right.move(Board.Action.RIGHT);
        }
    }

    /**
     * Since we're trying to avoid building too high,
     * we're going to give higher scores to Boards with
     * MaxHeights close to 0.
     */

    private int ohs(Board newBoard){
        int w = newBoard.getWidth();
        int counter = 0;
        for(int i=0;i<w;i++)
        {
            int maxColHeight = newBoard.getColumnHeight(i);
            if(maxColHeight>=1)
            {
                for(int j=0;j<maxColHeight;j++)
                {
                    if(newBoard.getGrid(i,j)==null)
                    {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    private int scoreBoard(Board newBoard) {
        return 200 - newBoard.getMaxHeight();
    }

}
