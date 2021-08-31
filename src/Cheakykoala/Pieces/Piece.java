package Cheakykoala.Pieces;

import Cheakykoala.*;

import java.util.ArrayList;

public abstract class Piece {
    int[] valueTable;
    Position position;
    char piece;
    char letter;
    Color color;
    boolean hasMoved = false;

    public Piece() {

    }

    public double getPieceEval() {
        this.letter = Character.toUpperCase(this.letter);
        switch (this.letter) {
            case 'R':
                return 500;
            case 'N':
            case 'B':
                return 300;
            case 'Q':
                return 900;
            case 'P':
                return 100;
        }
        return 0;
    }

    public boolean isOppositeColor(Piece piece) {
        return (this.color == Color.w && piece.getColor() == Color.b) || (this.color == Color.b && piece.getColor() == Color.w);
    }

    public int[] getValueTable(){
        return valueTable;
    }

    public int getValueInt(int x){
        return valueTable[x];
    }

    public boolean isSameColor(Piece piece) {
        return color == piece.getColor();
    }

    public abstract ArrayList<Move> getMoves(Board board);

    public abstract boolean canMove(Board board);

    public ArrayList<Move> getPromotionMoves(Board board) {
        return null;
    }

    public boolean isKing() {
        return false;
    }

    public boolean isQueen() {
        return false;
    }

    public boolean isBishop() {
        return false;
    }

    public boolean isKnight() {
        return false;
    }

    public boolean isPawn() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isRook() {
        return false;
    }

    public char getPiece() {
        return piece;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void promotionMove(Move move, Board board) {
        board.addPiece(move.getEnd(), move.getPiece());
        board.addPiece(move.getBeginning(), new Empty(move.getBeginning()));
        board.setCanEnpassant(false);
    }

    public void castleMove(Move move, Board board) {
        int x;
        int x1;
        int x2;
        int y = move.getBeginning().getY();
        if (move.getEnd().getX() < 4) {
            x = 3;
            x1 = 2;
            x2 = 0;
        } else {
            x = 5;
            x1 = 6;
            x2 = 7;
        }
        Position movedTo = new Position(x1, y);
        board.addPiece(movedTo, this);
        board.addPiece(move.getBeginning(), new Empty(move.getBeginning()));
        movedTo = new Position(x, y);
        board.addPiece(movedTo, board.get(x2, y));
        Position movedFrom = new Position(x2, y);
        board.addPiece(movedFrom, new Empty(movedFrom));
        board.setCanEnpassant(false);
    }

    public void inPassingMove(Move move, Board board) {
        Position taken = new Position(move.getEnd().getX(), move.getBeginning().getY());
        board.addPiece(move.getEnd(), new Pawn(color, move.getEnd()));
        board.addPiece(move.getBeginning(), new Empty(move.getBeginning()));
        board.addPiece(taken, new Empty(taken));
        board.setCanEnpassant(false);
//        System.out.println ("We just did a inPassingMove");
    }

    public void upTwoMove(Move move, Board board) {
        normalMove(move, board);
        int direction;
        if (color == Color.w)
            direction = -1;
        else
            direction = 1;
        Position inPassingSquare = new Position(move.getBeginning().getX(), move.getBeginning().getY() + direction);
        board.setInPassingSquare(inPassingSquare);
        board.setCanEnpassant(true);
    }

    public void normalMove(Move move, Board board) {
        board.addPiece(move.getEnd(), this);
        this.setPosition(move.getEnd());
        board.addPiece(move.getBeginning(), new Empty(move.getBeginning()));
        board.setCanEnpassant(false);
    }

    public void move(Board board, Move move) {
        board.setMoveState(this, move);
        board.changeEval(move, board.getPieceAt(move.getBeginning()));
        if (move.isPromotionMove(move)) {
            promotionMove(move, board);
        } else if (move.isCastleMove(board)) {
            castleMove(move, board);
        } else if (move.isInPassingMove(board)) {
            inPassingMove(move, board);
            board.increaseCaptures();
        } else if (move.isUpTwoMove(board)) {
            upTwoMove(move, board);
        } else {
            normalMove(move, board);
            if (move.isCapture(board)){
                board.increaseCaptures();
            }
        }
    }

    public char getLetter() {
        return this.letter;
    }
}
