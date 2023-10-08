package assignment;

import java.awt.*;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for its
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do pre-computation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {
    PieceType curr;
    Point[] body;
    int width;
    int height;
    int rotation_index = 0;
    Point[][] rotationConfigurations; 
    int[][] skirts;

    /*
     * Precompute each piece's rotation configuration
     */
    private Point[][] createRotationConfiguration(){
        Point configurations[][] = new Point[4][5];
        skirts = new int[4][this.getWidth()];

        configurations[0] = body;
        for(int i = 0; i < 4; i++){
            // create rotation configuration
            if(i != 0){
                configurations[i] = rotation(configurations[i - 1]);
            }

            // create skirt configurations
            int[] newskirt = new int[this.getWidth()];
            for(int j = 0; j < width; j++){
                newskirt[j] = Integer.MAX_VALUE;
            }
            for(Point p : configurations[i]){
                int px = (int) p.getX();
                int py = (int) p.getY();
                newskirt[px] = Math.min(newskirt[px], py);
            }
            skirts[i] = newskirt;
        }
        return configurations;
    }

    /*
     * Change points in in pieceBody[] so that it returns a rotated piece array
     */
    public Point[] rotation(Point[] pieceBody){ 
        Point[] newMapping = new Point[body.length]; 

    
        int xModifier = 1;
        int yModifier = -1;
        int xShift = 0;
        int yShift = 1;
        
        for(int i = 0; i < pieceBody.length; i++){
            Point p = pieceBody[i];
            int tempX = (int) p.getX();
            int tempY = (int) p.getY();
            int newX = xModifier * tempY + xShift * (width - 1);
            int newY = yModifier * tempX + yShift * (width - 1); 
            newMapping[i] = new Point(newX, newY);
        }
        return newMapping;
    }

    /**
     * Initialize piece and call constructor methods.
     */
    public TetrisPiece(PieceType type) {
        if(type == null){
            System.err.println("Recieved null type");
            return;
        }
        curr=type;
        body = curr.getSpawnBody();
        width = (int) curr.getBoundingBox().getWidth();
        height = (int) curr.getBoundingBox().getHeight();
        rotationConfigurations = createRotationConfiguration();
    }

    /*
     * O(1) access methods.
     */
    @Override
    public PieceType getType() {
        return curr;
    }

    @Override
    public int getRotationIndex() {
        return rotation_index;
    }

    @Override
    public Piece clockwisePiece() {
        rotation_index = (rotation_index + 5) % 4;
        body = rotationConfigurations[rotation_index];
        int temp = width;
        width = height;
        height = temp;
        return this;
    }

    @Override
    public Piece counterclockwisePiece() {
        rotation_index = (rotation_index + 3) % 4;
        body = rotationConfigurations[rotation_index];
        int temp = width;
        width = height;
        height = temp;
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Point[] getBody() {
        return body;
    }

    @Override
    public int[] getSkirt() {
        return skirts[rotation_index];
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;
        if(rotation_index == otherPiece.getRotationIndex() && curr == otherPiece.getType()){
            return true;
        }
        return false;
    }
}

