package Cheakykoala.Pieces;

import Cheakykoala.Color;
import Cheakykoala.Position;
import Cheakykoala.Board;
import Cheakykoala.Move;

import java.util.ArrayList;

public abstract class Piece {

    Position position;
    char piece;
    char letter;
    Color color;

    Piece(Piece piece){

    }

    public Piece() {

    }

    public Color getOppositeColor(){
        if (this.color == Color.g){
            return Color.g;
        }
        else if(this.color == Color.w){
            return Color.b;
        } else {
            return Color.w;
        }
    }

    public boolean isOtherColor(Piece piece){
        if (this.color != piece.getColor()){
            return true;
        }
        else
            return false;
    }
    public boolean isOppositeColor(Piece piece){
        return (this.color == Color.w && piece.getColor() == Color.b) || (this.color == Color.b && piece.getColor() == Color.w);
    }

    public boolean isSameColor(Piece piece) {
        return color == piece.getColor();
    }

    public abstract ArrayList<Move> getMoves(Board board);

    public abstract ArrayList<Move> getMovesNotCheck(Board board);

    public boolean isKing(){
        return false;
    }

    public boolean isPawn(){
        return false;
    }

    public boolean isEmpty(){
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

    public void newPiece(Color color, Position position){
        this.color = color;
    }

    public char getChar(){
        return this.piece;
    }

    public void setPosition(Position position){
        this.position = position;
    }

    public void move(Board board, Move move) {
            if (this.isPawn() && move.getEnd() == Board.getInPassingSquare() && Board.getCanEnpassant()){
                Position enPassantSquare = new Position(move.getEnd().getX(), move.getEnd().getY() + 1);
                board.addPiece(enPassantSquare, new Empty(move.getBeginning()));
            }
            board.addPiece(move.getEnd(), this);
            this.position = move.getEnd();
            board.addPiece(move.getBeginning(), new Empty(move.getBeginning()));
            if (this.isPawn() && move.getEnd().getY()  == 3 || move.getEnd().getY() == 4){
                if (move.getEnd().getY()  == 3){
                    Position placeHolder = new Position(move.getEnd().getX(), move.getEnd().getY() - 1);
                    board.setInPassingSquare(placeHolder);
                } else{
                    Position placeHolder = new Position(move.getEnd().getX(), move.getEnd().getY() + 1);
                    board.setInPassingSquare(placeHolder);
                }
                board.setCanEnpassant(true);
                return;
            }
        board.setCanEnpassant(false);
    }
//    public void undoMove(Board board, Move move, Piece taken) {
//        board.addPiece(move.getBeginning(), this);
//        this.position = move.getBeginning();
//        board.addPiece(move.getEnd(), taken);
//        taken.setPosition(move.getEnd());
//    }

    public char getLetter(){
        return this.letter;
    }
}
