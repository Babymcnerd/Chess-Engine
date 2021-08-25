package Cheakykoala;

import Cheakykoala.Pieces.*;

import java.util.HashMap;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static int index = 0;
    public static Color minimaxColor = Color.w;
    public static long startTime = System.currentTimeMillis();
    public static boolean timeout = false;
    public static int TIMEOUT_TIME = 60000;

    public static void main(String[] args) throws InterruptedException {
        Board board = new Board();
        apiConnect(board);
//        timeMinimax();
    }

    public static void apiConnect(Board board) {
        Scanner consoleInput = new Scanner(System.in);
        while (true) {
//            board.printBoard();
            String input = consoleInput.nextLine();
            if (input.contains("go")) {
                System.out.println(onGo(board));
            } else if (input.contains("uci")) {
                System.out.println("uciok");
            } else if (input.contains("isready")) {
                System.out.println("readyok");
            } else if (input.contains("position")) {
                UCIPosition(board, input);
            }
        }
    }

    public static void UCIPosition(Board board, String UCIPosition) {
        int startMoves = 3;
        index = 0;
        String[] UCIStringArray = UCIPosition.split(" ");
        if (UCIStringArray[1].equals("startpos")) {
            board.importBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            board.setBoardEval(evalBoard(board));
        } else {
            int i = 2;
            String fenString = "";
            fenString = UCIStringArray[1];
            while (!(UCIStringArray[i].equals("moves"))) {
                fenString += " " + UCIStringArray[i];
                i++;
            }
            fenString.stripLeading();
            //position k7/5P2/8/8/8/8/8/K7 w - - 0 1 moves f7f8
            board.importBoard(fenString);
            board.setBoardEval(evalBoard(board));
            board.printBoard();
            startMoves = i + 1;
        }
        for (int i = startMoves; i < UCIStringArray.length; i++) {
            index++;
            Move move;
            Position first = new Position(charToInt(UCIStringArray[i].charAt(0)), 8 - Character.getNumericValue(UCIStringArray[i].charAt(1)));
            Position second = new Position(charToInt(UCIStringArray[i].charAt(2)), 8 - Character.getNumericValue(UCIStringArray[i].charAt(3)));


            if (UCIStringArray[i].length() == 5) {
                char letter = UCIStringArray[i].charAt(4);
                move = new PromotionMove(first, second, makePiece(second, letter));


            } else {
                move = new Move(first, second);
            }
//            System.out.println (board.getBoardEval());
            board.getPieceAt(first).move(board, move);
            if (index % 2 == 0) {
                minimaxColor = Color.w;
            } else {
                minimaxColor = Color.b;
            }
        }
    }

    public static String onGo(Board board) {
        timeout = false;
        int INITIAL_DEPTH = 3;
        int CURRENT_DEPTH = 0;
        startTime = System.currentTimeMillis();
        Move bestMove = moveMinimax(board, 1, minimaxColor);
        for (int i = 0; ; i++) {
            if (timeout) {
                break;
            }
            CURRENT_DEPTH = INITIAL_DEPTH + i;
            System.out.println("current depth is " + CURRENT_DEPTH);
            Move checkMove = moveMinimax(board, CURRENT_DEPTH, minimaxColor);
            if (checkMove != null) {
                bestMove = checkMove;
            }
        }
//        System.out.println(System.currentTimeMillis() - startTime);
        System.out.println(board.getBoardEval());
        if (bestMove.isPromotionMove(bestMove)) {
            return new StringBuilder().append("bestmove ").append(bestMove.getBeginning().convertPosition()).append(bestMove.getEnd().convertPosition()).append(bestMove.getPiece().getLetter()).toString();
        }
        return new StringBuilder().append("bestmove ").append(bestMove.getBeginning().convertPosition()).append(bestMove.getEnd().convertPosition()).toString();
    }

    public static Move moveMinimax(Board board, int depth, Color color) {
        double bestMoveValue;
        boolean isMaxPlayer;
        if (color == Color.w) {
            bestMoveValue = Double.NEGATIVE_INFINITY;
            isMaxPlayer = false;
        } else {
            bestMoveValue = Double.POSITIVE_INFINITY;
            isMaxPlayer = true;
        }
        ArrayList<Move> bestMoves = new ArrayList<>();
        Move bestMove;
        Board child;
        for (Piece[] pieces : board.getBoard()) {
            for (Piece p : pieces) {
                if (p.getColor() == color) {
                    for (Move m : p.getMoves(board)) {
                        child = board.getChild(m);
                        double mx = minimax(child, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, isMaxPlayer);
                        if (mx == 123456)
                            return null;
                        if (bestMoveValue == mx) {
                            bestMoves.add(m);
                        }
                        if (color == Color.w) {
                            if (mx > bestMoveValue) {
                                bestMoveValue = mx;
                                bestMoves.clear();
                                bestMoves.add(m);
                            }
                        } else {
                            if (mx < bestMoveValue) {
                                bestMoveValue = mx;
                                bestMoves.clear();
                                bestMoves.add(m);
                            }
                        }
                    }
                }
            }
        }
        bestMove = bestMoves.get((int) (Math.random() * bestMoves.size()));
        return bestMove;
    }

    public static void timeMinimax() {
        Board board = new Board();
        board.importBoard("r2q1bnr/pp1p1kpp/1p1pn3/2b2p2/B2P2QP/1PP1NR2/P1N1B1P1/R3KP2 w Q - 0 1");
        board.setBoardEval(evalBoard(board));
        board.printBoard();
        double start = System.currentTimeMillis();
        System.out.println (moveMinimax(board, 4, Color.w));
        double end = System.currentTimeMillis();
        System.out.println((end - start) / 1000.0);
        System.out.println(board.getBoardEval());
    }

    public static double minimax(Board board, int depth, double alpha, double beta, boolean isMaxPlayer) {
        ArrayList<Move> moveList =  new ArrayList<>();
        ArrayList<Move> captureMoveList =  new ArrayList<>();
        double eval;
        double minEval;
        double maxEval;
        Color color;
        if (depth == 0) {
            return (board.getBoardEval());
        }
        if (isMaxPlayer) {
            color = Color.w;
            maxEval = Double.NEGATIVE_INFINITY;
            if (System.currentTimeMillis() - startTime > TIMEOUT_TIME){
                timeout = true;
                return 123456;
            }
            for (Piece[] pieces : board.getBoard()) {
                for (Piece piece : pieces) {
                    if (piece.getColor() == Color.w) {
                        for (Move move : piece.getMoves(board)) {
                            if (move.isCapture(board))
                                captureMoveList.add(move);
                            else
                                moveList.add(move);
                        }
                    }
                }
            }
            moveList.addAll(0,captureMoveList);
            if (moveList.isEmpty()){
                if (board.isColorInCheck(color))
                    return checkmateEval(color);
                else
                    return 0; // <--- this is most likely not right
            }
            for (Move m: moveList){
                eval = minimax(board.getChild(m), depth - 1, alpha, beta, false);
                maxEval = Math.max(alpha, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
//            sortArray(moveList);
            return maxEval;
        }
        else {
            color = Color.b;
            minEval = Double.POSITIVE_INFINITY;
            for (Piece[] pieces : board.getBoard()) {
                for (Piece piece : pieces) {
                    if (piece.getColor() == Color.b) {
                        for (Move move : piece.getMoves(board)) {
                            if (move.isCapture(board))
                                captureMoveList.add(move);
                            else
                                moveList.add(move);
                        }
                    }
                }
            }
            moveList.addAll(0,captureMoveList);
            if (moveList.isEmpty()){
                if (board.isColorInCheck(color))
                    return checkmateEval(color);
                else
                    return 0; // <--- this is most likely not right
            }
            for (Move m: moveList) {
                eval = minimax(board.getChild(m), depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
    //*-------------------------------with sorted by capture moves-----------------------------*//
//    public static double minimax(Board board, int depth, double alpha, double beta, boolean isMaxPlayer) {
//        ArrayList<Move> moveList =  new ArrayList<>();
//        ArrayList<Move> captureMoveList =  new ArrayList<>();
//        double eval;
//        double minEval;
//        double maxEval;
//        Color color;
//        if (isMaxPlayer)
//            color = Color.w;
//        else
//            color = Color.b;
////        }
//        if (depth == 0) {
//            return (evalBoard(board));
//        }
//        if (isMaxPlayer) {
//            maxEval = Double.NEGATIVE_INFINITY;
////            if (System.currentTimeMillis() - startTime > TIMEOUT_TIME){
////                timeout = true;
////                return 123456;
////            }
//            for (Piece[] pieces : board.getBoard()) {
//                for (Piece piece : pieces) {
//                    if (piece.getColor() == Color.w) {
//                        for (Move move : piece.getMoves(board)) {
//                            if (move.isCapture(board))
//                                captureMoveList.add(move);
//                            else
//                                moveList.add(move);
//                        }
//                    }
//                }
//            }
//            moveList.addAll(0,captureMoveList);
//            if (moveList.isEmpty()){
//                if (board.isColorInCheck(color))
//                    return checkmateEval(color);
//                else
//                    return 0; // <--- this is most likely not right
//            }
//            for (Move m: moveList){
//                eval = minimax(board.getChild(m), depth - 1, alpha, beta, false);
//                maxEval = Math.max(alpha, eval);
//                alpha = Math.max(alpha, eval);
//                if (beta <= alpha) {
//                    break;
//                }
//            }
////            sortArray(moveList);
//            return maxEval;
//        } else {
//            minEval = Double.POSITIVE_INFINITY;
//            for (Piece[] pieces : board.getBoard()) {
//                for (Piece piece : pieces) {
//                    if (piece.getColor() == Color.b) {
//                        for (Move move : piece.getMoves(board)) {
//                            if (move.isCapture(board))
//                                captureMoveList.add(move);
//                            else
//                                moveList.add(move);
//                        }
//                    }
//                }
//            }
//            moveList.addAll(0,captureMoveList);
//            if (moveList.isEmpty()){
//                if (board.isColorInCheck(color))
//                    return checkmateEval(color);
//                else
//                    return 0; // <--- this is most likely not right
//            }
//            for (Move m: moveList) {
//                eval = minimax(board.getChild(m), depth - 1, alpha, beta, true);
//                minEval = Math.min(minEval, eval);
//                beta = Math.min(beta, eval);
//                if (beta <= alpha) {
//                    break;
//                }
//            }
//            return minEval;
//        }
//    }

    public static double evalBoard(Board board) {
        int eval = 0;
        for (Piece[] pieces : board.getBoard()) {
            for (Piece piece : pieces) {
                if (piece.getColor() == Color.w) {
                    eval += getPieceEval(piece);
                    eval += getTableValue(piece, piece.getColor());
                } else {
                    eval -= getPieceEval(piece);
                    eval -= getTableValue(piece, piece.getColor());
                }
            }
        }
        return eval;
    }

    public static double getPieceEval(Piece piece) {
        char letter = Character.toUpperCase(piece.getLetter());
        switch (letter) {
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

    public static double checkmateEval(Color color) {
        if (color == Color.w)
            return Double.NEGATIVE_INFINITY;
        else
            return Double.POSITIVE_INFINITY;
    }

    public static int getTableValue(Piece piece, Color color) {
        int[] valueTable = piece.getValueTable();
        Position piecePosition = piece.getPosition();
        int x = piecePosition.getX();
        int y = piecePosition.getY();
        if (color == Color.w) {
            return valueTable[(y * 8) + x];
        } else if (color != Color.g) {

            return valueTable[63 - ((y * 8) + x)];
        }
        return 0;
    }

    public static void playHuman(Board board, Color color) {
        Scanner input = new Scanner(System.in);
        boolean wasLegal = false;
        while (!wasLegal) {
            System.out.print("Piece you want to move : ");
            String beginning = input.nextLine();
            System.out.println();
            System.out.print("Where you would so like to move your pieceage to sir : ");
            String end = input.nextLine();
            System.out.println();
            System.out.println(convertLetter(beginning.charAt(0)));
            System.out.println(8 - Character.getNumericValue(beginning.charAt(1)));

            Position first = new Position(convertLetter(beginning.charAt(0)), 8 - Character.getNumericValue(beginning.charAt(1)));
            Position last = new Position(convertLetter(end.charAt(0)), 8 - Character.getNumericValue(end.charAt(1)));
            if (new Move(first, last).isMoveLegal(board, color)) {
                wasLegal = true;
                board.getPieceAt(first).move(board, new Move(first, last));
                System.out.println("mediocre move");
                return;
            }
            System.out.println("GO BACK TO CHECKERS!");
        }
    }

    public static void playRandom(Board board, Color color) {
        ArrayList<Move> moves = new ArrayList();
        for (Piece[] pieces : board.getBoard()) {
            for (Piece p : pieces) {
                if (p.getColor() == color) {
                    for (Move m : p.getMoves(board)) {
                        moves.add(m);
                    }
                }
            }
        }
        int y = (int) (Math.random() * moves.size());
        board.getPieceAt(moves.get(y).getBeginning()).move(board, moves.get(y));
    }

    public static void playGame(int depth) throws InterruptedException {
        Board board = new Board();
        for (int i = 0; i < depth; i++) {
            board.printBoard();
            System.out.println();
            playRandom(board, Color.w);
            board.printBoard();
            System.out.println();
            moveMinimax(board, 3, Color.b);
        }
    }

    public static int countNodes(Board board, int depth, Color color) {
        int count = 0;
        Board child;
        if (depth == 0) {
            return 1;
        }
        for (Move move : board.getAllMoves(color)) {
            child = board.getChild(move);
            count += countNodes(child, depth - 1, getOppositeColor(color));
        }
        return count;
    }

    public static int convertLetter(char x) {
        return (x - 97);
    }

    public static char convertNumber(int x) {
        return ((char) (x + 97));
    }

    public static void debugger() {
        Board board = new Board();
        String fenString = "r3k2r/p1ppqNb1/bn2pnp1/3P4/1p2P3/2N2Q1p/PPPBBPPP/R3K2R b KQkq - 0 1";
        board.importBoard(fenString);
        board.setBoardEval(evalBoard(board));
        board.printBoard();
        int total = 0;
        int nodes = 0;
        Color color;
        if (fenString.contains("w")) {
            color = Color.w;
        } else
            color = Color.b;
        for (Piece[] pieces : board.getBoard()) {
            for (Piece p : pieces) {
                if (p.getColor() == color) {
                    for (Move m : p.getMoves(board)) {
                        System.out.print(m.getBeginning().convertPosition() + " -> " + m.getEnd().convertPosition() + " ");
                        nodes = countNodes(board.getChild(m), 2, getOppositeColor(color));
                        System.out.println(nodes);
                        total = total + nodes;
                    }
                }
            }
        }
        System.out.println(total);
    }

    public static void testNodes() {
        Board board = new Board();
        if (countNodes(board, 1, Color.w) == 20)
            System.out.println("depth 1");
        if (countNodes(board, 2, Color.w) == 400)
            System.out.println("depth 2");
        if (countNodes(board, 3, Color.w) == 8902)
            System.out.println("depth 3");
        if (countNodes(board, 4, Color.w) == 197281)
            System.out.println("depth 4");
        if (countNodes(board, 5, Color.w) == 4865609)
            System.out.println("depth 5");
    }

    public static void testNodes2() {
        Board board = new Board();
        ArrayList<String> fen = new ArrayList<>();
        fen.add("r6r/1b2k1bq/8/8/7B/8/8/R3K2R b KQ - 3 2");
        board.importBoard("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
        board.setBoardEval(evalBoard(board));
        Color color = Color.w;
        board.printBoard();
        Position position = new Position(4, 0);
        System.out.println(board.getPieceAt(position).getMoves(board));
        System.out.println(board.getPieceAt(position).getMoves(board).size());

    }

    public static Piece makePiece(Position position, char letter) {
        letter = Character.toUpperCase(letter);
        if (letter == 'Q')
            return new Queen(minimaxColor, position);
        if (letter == 'N')
            return new Knight(minimaxColor, position);
        if (letter == 'B')
            return new Bishop(minimaxColor, position);
        if (letter == 'R')
            return new Rook(minimaxColor, position);

        return new Empty(position);
    }

    public static int charToInt(char letter) {
        switch (letter) {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
        }
        return 999;
    }

    public static Color getOppositeColor(Color color) {
        if (color == Color.w) {
            return Color.b;
        }
        return Color.w;
    }
}