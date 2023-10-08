package assignment;

import java.awt.*;

import javax.swing.text.AbstractDocument.LeafElement;

import assignment.Piece.PieceType;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {

    // field variables
    Piece.PieceType[][] board;
    Piece currPiece = null;
    Point currCord = null;
    boolean filled[][];
    int[] uppermost; 
    int[] rowWidths;
    int board_width;
    int board_height;
    int maxHeight = 0; 
    int rowsCleared=0;
    Action last_act = Board.Action.NOTHING;
    Result lastResult = Result.NO_PIECE;

    /**
     * Helper method, this draws the objects 
     * Returns -1 if collides with object or object goes out of bounds
     */
    public void undrawShape(Piece piece, Point location) {
        for(Point currPoint : piece.getBody()) {
            int locx = (int)(location.getX()+currPoint.getX());
            int locy = (int)(location.getY()+currPoint.getY());
            if(locx >= 0 && locx < board_width && locy >= 0){ // upperbound
                board[locx][locy]=null;
            }
        }
    }

    /**
     * Checks to see if it's legal to draw a shape. Return -1 if no, draw otherwise
     */
    public int fillShape(Piece piece, Point location) {
        // check
        for(Point currPoint : piece.getBody()) {
            int locx = (int)(location.getX()+currPoint.getX());
            int locy = (int)(location.getY()+currPoint.getY());
            if(locx < 0 || locx >= board_width || locy < 0 || 
                locy >= board[locx].length || board[locx][locy] != null) {
                return -1; 
            }
        }
        // draw
        for(Point currPoint : piece.getBody()) {
            int locx = (int)(location.getX()+currPoint.getX());
            int locy = (int)(location.getY()+currPoint.getY());
            board[locx][locy]=piece.getType();
        }
        return 1;
    }

    /*
     * Determine whether or not a piece should be placed by attempting to draw it one space below
     * If the shape is unable to be drawn at a lower y level, the piece is placed.
     */
    public boolean placeOrNo(Piece piece, Point location){
        undrawShape(piece, location);
        Point newPoint = new Point((int) location.getX(),(int) location.getY() - 1);
        int returnCode = fillShape(piece, newPoint);
        if(returnCode == -1){
            fillShape(piece, location);
            return true;
        }
        undrawShape(piece, newPoint);
        fillShape(piece, location);
        return false; 
    }

    /*
     * Place an object, update uppermost[], maxHeight, and check to clear rows
     */
    public void place(Piece piece, Point location){
        //System.out.println("Piece placed"); // whitebox testing
        for(Point currPoint : piece.getBody()) {
            int locx = (int)(location.getX()+currPoint.getX());
            int locy = (int)(location.getY()+currPoint.getY());
            rowWidths[locy]++;
            if(locy>uppermost[locx])
            {
                uppermost[locx]=locy;
            }
        }

        clearRow();
        for(int c = 0; c < board_width; c++){
            uppermost[c] = -1;
            for(int r = 0; r < board_height; r++){
                if(board[c][r] != null){
                    uppermost[c] = r;
                }
            }
        }
        maxHeight = 0;
        for(int i : uppermost){
                maxHeight = Math.max(maxHeight,i + 1);
        }
        currCord = null; currPiece = null;
        //System.out.println("Max height: " + this.getMaxHeight()); // whitebox testing
    }

    /*
     * Clear rows and update shift higher rows down
     */
    public void clearRow(){
        for(int j = 0;j<board_height;j++) {
            if(rowWidths[j]==board_width) {
                //System.out.println("Counter++ " + rowsCleared); // whitebox testing
                rowsCleared++;
                for(int i=0;i<board_width;i++)
                {
                    board[i][j]=null;
                }
                for(int k=j+1;k<board_height;k++)
                {
                    rowWidths[k-1]=rowWidths[k];
                    for(int i=0;i<board_width;i++)
                    {
                        board[i][k-1]=board[i][k];
                    }
                }
                j--;
            }
        }
    }

    // helper functions for modifying the testBoards
    public void setGrid(int x, int y, Piece.PieceType type){
        if(type == null){
            board[x][y] = null;
            return;
        }
        TetrisPiece temp = new TetrisPiece(type);
        board[x][y] = temp.getType();
    }

    public void setCurrPiece(Piece piece){
        currPiece = piece;
    }

    public void setCurrCord(Point position){
        currCord = position; 
    }

    public void setColumnsAndMax(Board refBoard){
        for(int i = 0; i < board_width; i++){
            uppermost[i] = refBoard.getColumnHeight(i) - 1;
        }
        maxHeight = refBoard.getMaxHeight();
    }
    
    /*
     * Constructor that initializes field variables.
     */
    public TetrisBoard(int width, int height) {
        board = new Piece.PieceType[width][height];
        uppermost = new int[width];
        rowWidths = new int[height];

        for(int i = 0; i < width; i++){
            uppermost[i] = -1;
        }

        board_width = width;
        board_height = height;
        maxHeight = 0;
        rowsCleared = 0;
        last_act = Action.NOTHING;
        lastResult = Result.NO_PIECE;

    }
    /*
     * Determines how the board behaves for a given action 
     * 
     * A general blueprint for each case: 
     *      1. Test to see if move is possible
     *      2. Execute if can
     *      3. Update field variables
     */
    @Override
    public Result move(Action act) {
        if(act == null){
            act = Board.Action.NOTHING;
        }
        last_act = act;
        if(currCord == null || currPiece == null) {
            lastResult = Result.NO_PIECE;
            System.err.println("RETURING NO_PIECE BC NULL");
            return Result.NO_PIECE;
        }
        Point newPoint;
        int returnCode;
        switch(act) {
            case LEFT: // follows blueprint
                undrawShape(currPiece, currCord);
                newPoint = new Point((int) currCord.getX()-1,(int) currCord.getY());
                returnCode = fillShape(currPiece, newPoint);
                if(returnCode == -1) {
                    fillShape(currPiece, currCord);
                    lastResult = Result.OUT_BOUNDS;
                    return Result.OUT_BOUNDS;
                }
                if(returnCode == 1) {
                    currCord = newPoint;
                    if(placeOrNo(currPiece, currCord)){
                        place(currPiece, currCord);
                        lastResult = Result.PLACE;
                        return Result.PLACE;
                    }
                    lastResult = Result.SUCCESS;
                    return Result.SUCCESS;
                }
                break;
            case RIGHT: // follows blueprint
                undrawShape(currPiece, currCord);
                newPoint = new Point((int) currCord.getX()+1,(int) currCord.getY());
                returnCode = fillShape(currPiece, newPoint);
                if(returnCode == -1) {
                    fillShape(currPiece, currCord);
                    lastResult = Result.OUT_BOUNDS;
                    return Result.OUT_BOUNDS;
                }
                if(returnCode == 1) {
                    currCord = newPoint;
                    if(placeOrNo(currPiece, currCord)){
                        place(currPiece, currCord);
                        lastResult = Result.PLACE;
                        return Result.PLACE;
                    }
                    lastResult = Result.SUCCESS;
                    return Result.SUCCESS;
                }
                break;
            case DOWN:
                newPoint = new Point((int) currCord.getX(),(int) currCord.getY() - 1);
                // can the piece move down?
                if(placeOrNo(currPiece, currCord)){ // no -> place the piece where it is
                    place(currPiece, currCord);
                    lastResult = Result.PLACE;
                    return Result.PLACE;
                }
                else{ // yes -> move it down
                    undrawShape(currPiece, currCord);
                    fillShape(currPiece, newPoint);
                    currCord = newPoint;
                    if(placeOrNo(currPiece, currCord)){ // check to see if the object should be placed
                        place(currPiece, currCord);
                        lastResult = Result.PLACE;
                        return Result.PLACE;
                    }
                    lastResult = Result.SUCCESS;
                    return Result.SUCCESS;
                }
            case DROP: // essentially call DOWN until the piece is placed
                undrawShape(currPiece, currCord);
                returnCode = 2; 
                while(returnCode != -1) {
                    undrawShape(currPiece, currCord);
                    newPoint = new Point((int) currCord.getX(),(int) currCord.getY() - 1);
                    returnCode = fillShape(currPiece, newPoint);
                    if(returnCode != -1){
                        currCord = newPoint;
                    }
                }
                fillShape(currPiece, currCord);
                place(currPiece, currCord);
                lastResult = Result.PLACE;
                return Result.PLACE;
            case CLOCKWISE: // while the object hasn't been rotated, loop through the wallkick indexes
                undrawShape(currPiece, currCord);
                returnCode = -1;
                int wallkick_index = 0;
                int start_rot_index = currPiece.getRotationIndex();
                currPiece = currPiece.clockwisePiece();
                while(returnCode == -1){ // loop until the rotated object can be drawn
                    if(wallkick_index < 5){ // try each wall kick sequentially
                        int x_shift;
                        int y_shift;
                        if(currPiece.getType() != Piece.PieceType.STICK){
                            x_shift = (int) Piece.NORMAL_CLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getX();
                            y_shift = (int) Piece.NORMAL_CLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getY(); 
                        }
                        else{
                            x_shift = (int) Piece.I_CLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getX();
                            y_shift = (int) Piece.I_CLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getY();
                        }
                        newPoint = new Point((int) currCord.getX() + x_shift, (int) currCord.getY() + y_shift);
                        returnCode = fillShape(currPiece, newPoint);
                        wallkick_index++;
                        if(returnCode != -1){ // found a configuration
                            currCord = newPoint;
                            if(placeOrNo(currPiece, currCord)){
                                place(currPiece, currCord);
                                lastResult = Result.PLACE;
                                return Result.PLACE;
                            }
                            lastResult = Result.SUCCESS;
                            return Result.SUCCESS;
                        }
                    }
                    else{ // no wallkick position is viable
                        currPiece = currPiece.counterclockwisePiece();
                        returnCode = fillShape(currPiece, currCord);
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                }
            case COUNTERCLOCKWISE: // while the object hasn't been rotated, loop through the wallkick indexes
                undrawShape(currPiece, currCord);
                returnCode = -1;
                wallkick_index = 0;
                start_rot_index = currPiece.getRotationIndex();
                currPiece = currPiece.counterclockwisePiece();
                while(returnCode == -1){ // loop until the rotated object can be drawn
                    if(wallkick_index < 5){ // try each wall kick sequentially
                        int x_shift;
                        int y_shift;
                        if(currPiece.getType() != Piece.PieceType.STICK){
                            x_shift = (int) Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getX();
                            y_shift = (int) Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getY();
                            
                        }
                        else{
                            x_shift = (int) Piece.I_COUNTERCLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getX();
                            y_shift = (int) Piece.I_COUNTERCLOCKWISE_WALL_KICKS[start_rot_index][wallkick_index].getY();
                        }
                        newPoint = new Point((int) currCord.getX() + x_shift, (int) currCord.getY() + y_shift);
                        returnCode = fillShape(currPiece, newPoint);
                        if(returnCode != -1){ // found a configuration
                            currCord = newPoint;
                            if(placeOrNo(currPiece, currCord)){
                                place(currPiece, currCord);
                                lastResult = Result.PLACE;
                                return Result.PLACE;
                            }
                            lastResult = Result.SUCCESS;
                            return Result.SUCCESS;
                        }
                        wallkick_index++;
                    }
                    else{ // no wallkick position is viable
                        currPiece = currPiece.clockwisePiece();
                        returnCode = fillShape(currPiece, currCord);
                        lastResult = Result.OUT_BOUNDS;
                        return Result.OUT_BOUNDS;
                    }
                }
            case NOTHING: 
                lastResult = Result.SUCCESS;
                return Result.SUCCESS;
            case HOLD: // karma
                lastResult = Result.OUT_BOUNDS;
                return Result.OUT_BOUNDS;
        }
        lastResult = Result.NO_PIECE;
        return Result.NO_PIECE; 
    }

    /*
     * Create a new board, piece, and piece cordinate. Update the new board's hidden values and pass these new objects 
     * to the new board. Finally, invoke the action on the new board. 
     */
    @Override
    public Board testMove(Action act) { 
        if(act == null){
            System.err.println("Null object passed to testMove");
            return null;
        }
        TetrisBoard testBoard = new TetrisBoard(board_width, board_height);
        for(int x = 0; x < board_width; x++){
            for(int y = 0; y < board_height; y++){
                testBoard.setGrid(x, y, board[x][y]);
            }
        }
        TetrisPiece otherPiece;
        Point otherCord;
        if(this.getCurrentPiece() == null){
            return testBoard;
        }
        else{
            otherPiece = new TetrisPiece(currPiece.getType());
            for(int i = 0; i < currPiece.getRotationIndex(); i++){
                otherPiece.clockwisePiece();
            }
            if(!currPiece.equals(otherPiece)){
                System.err.println("Piece duplication error");
            }
            otherCord = new Point((int) currCord.getX(), (int) currCord.getY());
        }
        
        testBoard.setCurrPiece(otherPiece);
        testBoard.setCurrCord(otherCord);
        testBoard.setColumnsAndMax(testBoard);

        if(!this.equals(testBoard)){
            System.err.println("Test board is not equal");
        }
        testBoard.move(act);
        return testBoard;
    }

    // these functions have O(1) return time
    @Override
    public Piece getCurrentPiece() { 
        return currPiece; 
    }

    @Override
    public Point getCurrentPiecePosition() { 
        return currCord; 
    }
    
    /*
     * Check to see if the piece can be placed. Throw an error otherwise.
     */
    @Override
    public void nextPiece(Piece p, Point spawnPosition) throws IllegalArgumentException {
        if(p == null || spawnPosition == null){
            System.err.println("Null pointer in nextPiece");
            return;
        }
        TetrisPiece newPiece = new TetrisPiece(p.getType());
        int errorCode = fillShape(newPiece, spawnPosition);
        if(errorCode == -1) { 
            IllegalArgumentException exception = new IllegalArgumentException("Error in placement");
            System.err.println("Error in placement, argument thrown");
            throw exception;
        }
        currPiece = newPiece;
        currCord = spawnPosition;
    }

    /*
     * Checks for null input, then checks to see if the current piece, position, and grid are equal.
     */
    @Override
    public boolean equals(Object other) { 
        if(other == null) return false;
        if(!(other instanceof TetrisBoard)) return false;
        TetrisBoard otherBoard = (TetrisBoard) other;
        if(this.getCurrentPiece() == null){
            if(otherBoard.getCurrentPiece() != null){
                return false;
            }
        }
        else if(!this.getCurrentPiece().equals(otherBoard.getCurrentPiece())){
            System.err.println("Not equal in piece");
            return false;
        }
        if(this.getCurrentPiecePosition() == null){
            if(otherBoard.getCurrentPiecePosition() != null){
                return false;
            }
        }
        else if(this.getCurrentPiecePosition().getX() != otherBoard.getCurrentPiecePosition().getX() ||
            this.getCurrentPiecePosition().getY() != otherBoard.getCurrentPiecePosition().getY()){
            System.err.println("Not equal in position");
            return false;
        }
        if(this.getWidth() != otherBoard.getWidth() || this.getHeight() != otherBoard.getHeight()){
            return false;
        }
        for(int x = 0; x < otherBoard.getWidth(); x++){
            for(int y = 0; y < otherBoard.getHeight(); y++){
                if(otherBoard.getGrid(x, y) != this.getGrid(x, y)){
                    System.err.println("Not equal in grid");
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * O(1) functions
     */

    @Override
    public Result getLastResult() { // done
        return lastResult; 
    } 

    @Override
    public Action getLastAction() { //done
        return last_act; 
    } 

    @Override
    public int getRowsCleared() { // done
        return rowsCleared; 
    }

    @Override
    public int getWidth() { // done
        return board_width; 
    } 

    @Override
    public int getHeight() { // done
        return board_height; 
    } 

    @Override
    public int getMaxHeight() { // done
        return maxHeight; 
    } 

    /*
     * Loop through skirt. Find the max Y rest value, where the Y rest value is uppermost[x] - skirt[x] + 1.
     */

    @Override
    public int dropHeight(Piece piece, int x) { 
        if(piece == null){
            System.err.println("Null piece on drop height");
            return -1;
        }
        int skirt[] = piece.getSkirt();
        int ans = 0;
        for(int i = 0; i < skirt.length; i++) {
            if(x + i < 0 && skirt[i] == Integer.MAX_VALUE){
                continue;
            }
            if(x + i < 0){
                System.err.println("Dropped out of bounds");
                return -1;
            }
            if(x + i >= board_width && skirt[i] != Integer.MAX_VALUE){
                System.err.println("Dropped out of bounds");
                return -1;
            }
            if(x + i >= board_width){
                continue;
            }
            int restY = Math.max(uppermost[x + i] - skirt[i] + 1, 0);
            ans = Math.max(ans, restY);
        }
        return ans;
    }

    @Override
    public int getColumnHeight(int x) { // done
        if(x >= this.getWidth() || x < 0){
            System.err.println("Index out of bounds on get column height");
            return -1;
        }
        return uppermost[x] + 1;
    } 

    @Override
    public int getRowWidth(int y) { // done
        if(y >= this.getHeight() || y < 0){
            System.err.println("Index out of bounds on get row width");
            return -1;
        }
        return rowWidths[y];
    }

    @Override
    public Piece.PieceType getGrid(int x, int y) { // done
        if(y >= this.getHeight() || y < 0){
            System.err.println("Index out of bounds on get grid");
            return null;
        }
        if(x >= this.getWidth() || x < 0){
            System.err.println("Index out of bounds on get grid");
            return null;
        }
        return board[x][y]; 
    } 
}
