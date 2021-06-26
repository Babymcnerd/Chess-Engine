package Cheakykoala.Pieces;

import Cheakykoala.Board;
import Cheakykoala.Color;
import Cheakykoala.Move;
import Cheakykoala.Position;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(Color c, Position position) {
        super();
        this.position = position;
        this.color = c;
        if (c == Color.w) {
            piece = (char) 0x265C;
        } else {
            piece = (char) 0x2656;
        }
    }

    public ArrayList<Move> getMoves(Board board) {
        ArrayList<Move> moves = new ArrayList<>();
        int[][] baseMoves = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1},
        };
        for (int[] arr : baseMoves) {
            Position checkPosition = new Position(position.getX() + arr[0], position.getY() + arr[1]);
            Move move = new Move(position, checkPosition);
            while (move.isMoveLegal(board, color)) {
                move = new Move(position, checkPosition);
                moves.add(move);
                if (board.getPieceAt(checkPosition).getColor() != Color.g)
                    break;
                checkPosition = new Position(checkPosition.getX() + arr[0], checkPosition.getY() + arr[1]);
                move = new Move(position, checkPosition);
            }
        }
        return moves;
    }
}

