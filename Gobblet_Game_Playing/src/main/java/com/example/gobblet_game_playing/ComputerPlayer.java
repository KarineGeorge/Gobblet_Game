package com.example.gobblet_game_playing;
import com.example.gobblet_game_playing.Game.Difficulty;

import java.util.ArrayList;

public class ComputerPlayer extends Player {

    private Difficulty difficulty;
    private GobbletColor[] gobbletColors = GobbletColor.values();
    private Game.Turn[] turns = Game.Turn.values();
    private GameMove bestMove;
    private int searchDepth;
    public int pruneCounter = 0;
    private TreeNode currentRoot = null;

    public ComputerPlayer(String name, GobbletColor gobbletColor, Difficulty difficulty) {
        super(name, gobbletColor);
        this.difficulty = difficulty;
        if (difficulty == Difficulty.EASY) {
            searchDepth = 1;
        } else if (difficulty == Difficulty.NORMAL) {
            searchDepth = 3;
        } else if (difficulty == Difficulty.HARD){
            searchDepth = 5;
        }
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    // Returns move played by the computer
    public GameMove playGobbletMove(Board board,Game.Turn turn) {
        bestMove = null;
        bestMove = iterativeDeepening(board,turn,searchDepth);
//        currentRoot.printChildren(currentRoot);
        return bestMove;
    }


    Board generateBoardState(Board board, GameMove possibleMove, Game.Turn turn) {

        Board newBoardState = new Board(board);
        newBoardState.playRound(new GameMove(possibleMove), turn);
        return newBoardState;

    }

    ArrayList<GameMove> generatePossibleMoves(Board myBoard, Game.Turn turn) {

        ArrayList<GameMove> possibleMoves = new ArrayList<>();
        ArrayList<Boolean> flags = new ArrayList<>(4);

        /* Initialize the flags ArrayList */
        for (int i = 0; i < 4; i++) {
            flags.add(false);
        }
        for (int i = 0; i < 3; i++) {

            if (myBoard.getPlayersGobblets()[turn.ordinal()][i].isEmpty()) {
                continue;
            }

            if (flags.get(myBoard.getPlayersGobblets()[turn.ordinal()][i].peek().getGobbletSize().ordinal())) {
                continue;
            } else {
                flags.set(myBoard.getPlayersGobblets()[turn.ordinal()][i].peek().getGobbletSize().ordinal(), true);
            }

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    Gobblet gobblet = new Gobblet(myBoard.getPlayersGobblets()[turn.ordinal()][i].peek());
                    GameMove possibleMove = new GameMove(gobblet, x, y, i);

                    if (this.isValidMove(possibleMove, myBoard, turn)) {

                        possibleMoves.add(possibleMove);

                    }
                }
            }
        }

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {

                /* There is no Gobblet to move or its the other Player's Gobblet */
                if (myBoard.getFront(x, y) == null || myBoard.getFront(x, y).getGobbletColor().ordinal() != gobbletColors[turn.ordinal()].ordinal()) {
                    continue;
                }


                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 4; j++) {

                        if (x == i && y == j) {
                            continue;
                        }
                        Gobblet gobblet = new Gobblet(myBoard.getFront(x, y));

                        GameMove possibleMove = new GameMove(gobblet, i, j, -1);

                        if (this.isValidMove(possibleMove, myBoard, turn)) {

                            possibleMoves.add(possibleMove);

                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    public int alphaBeta(Board board, Game.Turn turn, int alpha, int beta, int depth, boolean isMaximizingPlayer, TreeNode treeNode) {

        GobbletColor winnerColor = board.isWinningState(playerColor);

        if (depth == 0 || winnerColor != null) {
            int score = evaluation(board, playerColor, depth) + depth;
            treeNode.setScore(score);
            return score;
        }

//        int rating = evaluation(board, playerColor, depth) + depth;
//
//        if(difficulty != Difficulty.EASY && depth != searchDepth && board.isCriticalState()) {
//            treeNode.setScore(rating);
//            return rating;
//        }

        if(treeNode.getChildren() == null){
            ArrayList<GameMove> possibleMoves = generatePossibleMoves(board, turn);
            treeNode.setChildren(new ArrayList<TreeNode>());
            for(GameMove move: possibleMoves){
                treeNode.getChildren().add(new TreeNode(move, isMaximizingPlayer));
            }
        }else{
            treeNode.sortChildren(isMaximizingPlayer);
        }

        Board boardState;

        if (isMaximizingPlayer) {
            int maxVal = Integer.MIN_VALUE;
            int i = 0;
//            TreeNode node : treeNode.getChildren()
            for (TreeNode node : treeNode.getChildren()) {
//                TreeNode node = treeNode.getChildren().get(n);
                boardState = generateBoardState(board, node.getMove(), turn);

                int val;
                if (turn.ordinal() == Game.Turn.A.ordinal()) {
                    val = alphaBeta(boardState, Game.Turn.B, alpha, beta, depth - 1, !isMaximizingPlayer, node);

                } else {
                    val = alphaBeta(boardState, Game.Turn.A, alpha, beta, depth - 1, !isMaximizingPlayer, node);
                }
                node.setScore(val);

                if (bestMove == null){
                    bestMove = treeNode.getChildren().get(0).getMove();
                }
                if (val > maxVal) {
                    if (depth == searchDepth) {
                        bestMove = treeNode.getChildren().get(i).getMove();
                    }
                    maxVal = val;
                }

                alpha = Math.max(alpha, val);
                if (beta <= alpha) {
//                    treeNode.children.remove(node);
                    pruneCounter++;
                    break;
                }
                i++;
            }

//            treeNode.sortChildren(isMaximizingPlayer);
            return maxVal;
        } else {
            int minVal = Integer.MAX_VALUE;
            for (TreeNode node : treeNode.getChildren()) {
//                TreeNode node = treeNode.getChildren().get(n);
                boardState = generateBoardState(board, node.getMove(), turn);

                int val;
                if (turn.ordinal() == Game.Turn.A.ordinal()) {
                    val = alphaBeta(boardState, Game.Turn.B, alpha, beta, depth - 1, !isMaximizingPlayer, node);
                } else {
                    val = alphaBeta(boardState, Game.Turn.A, alpha, beta, depth - 1, !isMaximizingPlayer, node);
                }
//                System.out.println(val);
                node.setScore(val);
//                System.out.println(node.getScore());


                minVal = Math.min(minVal, val);

                beta = Math.min(beta, val);

                if (beta <= alpha) {
//                    treeNode.children.remove(node);
                    pruneCounter++;
                    break;
                }

            }
//            for (int n = 0; n < treeNode.getChildren().size(); n++) {
//                TreeNode node = treeNode.getChildren().get(n);
//                System.out.println(node.getScore());
//            }
//            treeNode.sortChildren(isMaximizingPlayer);
//            for (TreeNode node : treeNode.getChildren()) {
//                System.out.println(node.getScore());
//            }
            return minVal;
        }
    }


    /*
     * Number of my played Gobblets in rows, columns and diagonals compared to my opponent
     * Size of my played Gobblets in rows, columns and diagonals compared to my opponent
     * States where my opponent has one play to win in hard difficulty mode (Critical Position Heuristic)
     * Winning state increases my score with +10000 * (depth + 1)
     * Losing state decreses my score with -10000 * (depth + 1)
     */
    public int evaluation(Board board, GobbletColor myColor, int depth) {

        int myScore = 0;
//        int opponentScore = 0;
//        int totalSizeScore = 0;
        int sizeScore = 0;
        boolean criticalPosition = false;


        for (int i = 0; i < 4; i++) {
            int myNewScore = 0;
            int opponentNewScore = 0;
//            int sizeScore = 0;
            myScore = 0;
            boolean isBig = false;
            for (int j = 0; j < 4; j++) {
                Gobblet gobblet = board.getFront(i, j);
                if (gobblet != null) {
                    if (gobblet.getGobbletColor() == myColor) {
//                        myScore++;
                        myNewScore++;
                        isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
                        sizeScore += gobblet.getGobbletSize().ordinal() * 5;

                    } else {
                        opponentNewScore++;
//                        myScore--;
                        sizeScore -= gobblet.getGobbletSize().ordinal() * 5;

                    }
                }



            }
            if(myNewScore != 0){
                myScore += (myNewScore - opponentNewScore);
//                totalSizeScore += sizeScore;
            }
            if(opponentNewScore == 3 && !isBig)
                criticalPosition = true;
        }


        for (int j = 0; j < 4; j++) {
            int myNewScore = 0;
            int opponentNewScore = 0;
//            int sizeScore = 0;
            boolean isBig = false;
            for (int i = 0; i < 4; i++) {
                Gobblet gobblet = board.getFront(i, j);
                if (gobblet != null) {
                    if (gobblet.getGobbletColor() == myColor) {
//                        myScore++;
                        myNewScore++;
                        isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
                        sizeScore += gobblet.getGobbletSize().ordinal() * 5;

                    } else {
                        opponentNewScore++;
//                        myScore--;
                        sizeScore -= gobblet.getGobbletSize().ordinal() * 5;

                    }
                }

            }
            if(myNewScore!=0){
                myScore += (myNewScore - opponentNewScore);
//                totalSizeScore += sizeScore;
            }
            if(opponentNewScore == 3 && !isBig)
                criticalPosition = true;
        }


        int opponentNewScore = 0;
        int myNewScore = 0;
//        int sizeScore = 0;
        boolean isBig = false;

        for (int i = 0; i < 4; i++) {
            Gobblet gobblet = board.getFront(i, i);
            if (gobblet != null) {
                if (gobblet.getGobbletColor() == myColor) {
//                    myScore++;
                    myNewScore++;
                    isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
                    sizeScore += gobblet.getGobbletSize().ordinal() * 5;

                } else {
                    opponentNewScore++;
//                    myScore--;
                    sizeScore -= gobblet.getGobbletSize().ordinal() * 5;

                }
            }
        }
        if(opponentNewScore == 3 && !isBig)
            criticalPosition = true;

        if(myNewScore != 0){
            myScore += (myNewScore - opponentNewScore);
//            totalSizeScore += sizeScore;
        }
        myNewScore = 0;
        opponentNewScore = 0;
//        sizeScore = 0;
        isBig = false;

        for (int i = 0; i < 4; i++) {
            Gobblet gobblet = board.getFront(i, 3 - i);
            if (gobblet != null) {
                if (gobblet.getGobbletColor() == myColor) {
//                    myScore++;
                    myNewScore++;
                    isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
                    sizeScore += gobblet.getGobbletSize().ordinal() * 5;

                } else {
                    opponentNewScore++;
//                    myScore--;
                    sizeScore -= gobblet.getGobbletSize().ordinal() * 5;

                }
            }
        }
        if(opponentNewScore == 3 && !isBig)
            criticalPosition = true;

        if(myNewScore != 0){
            myScore += (myNewScore - opponentNewScore);
//            totalSizeScore += sizeScore;
        }

        GobbletColor winner = board.isWinningState(playerColor);



        if(winner != null && winner == playerColor){
            myScore += (10000 * (depth + 1));
        }else if (winner != null){
            myScore -= (10000 * (depth + 1));
        } else if (difficulty != Difficulty.EASY && criticalPosition){
            myScore -= (10000 * (depth + 1));
        }

        board.setCriticalState(criticalPosition);

        return myScore + sizeScore;
    }

//    public int evaluation(Board board, GobbletColor myColor, int depth) {
//
//        int myScore = 0;
//        int sizeScore = 0;
//        boolean criticalPosition = false;
//
//
//        for (int i = 0; i < 4; i++) {
//            int opponentNewScore = 0;
//            boolean isBig = false;
//            for (int j = 0; j < 4; j++) {
//                Gobblet gobblet = board.getFront(i, j);
//                if (gobblet != null) {
//                    if (gobblet.getGobbletColor() == myColor) {
//                        myScore++;
//                        isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
//                        sizeScore += gobblet.getGobbletSize().ordinal() * 5;
//
//                    } else {
//                        opponentNewScore++;
//                        myScore--;
//                        sizeScore -= gobblet.getGobbletSize().ordinal() * 5;
//
//                    }
//                }
//
//            }
//
//            if(opponentNewScore == 3 && !isBig)
//                criticalPosition = true;
//        }
//
//
//        for (int j = 0; j < 4; j++) {
//            int opponentNewScore = 0;
//            boolean isBig = false;
//            for (int i = 0; i < 4; i++) {
//                Gobblet gobblet = board.getFront(i, j);
//                if (gobblet != null) {
//                    if (gobblet.getGobbletColor() == myColor) {
//                        myScore++;
//                        isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
//                        sizeScore += gobblet.getGobbletSize().ordinal() * 5;
//
//                    } else {
//                        opponentNewScore++;
//                        myScore--;
//                        sizeScore -= gobblet.getGobbletSize().ordinal() * 5;
//
//                    }
//                }
//            }
//            if(opponentNewScore == 3 && !isBig)
//                criticalPosition = true;
//        }
//
//
//        int opponentNewScore = 0;
//        boolean isBig = false;
//
//        for (int i = 0; i < 4; i++) {
//            Gobblet gobblet = board.getFront(i, i);
//            if (gobblet != null) {
//                if (gobblet.getGobbletColor() == myColor) {
//                    myScore++;
//                    isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
//                    sizeScore += gobblet.getGobbletSize().ordinal() * 5;
//
//                } else {
//                    opponentNewScore++;
//                    myScore--;
//                    sizeScore -= gobblet.getGobbletSize().ordinal() * 5;
//
//                }
//            }
//        }
//        if(opponentNewScore == 3 && !isBig)
//            criticalPosition = true;
//
//
//        opponentNewScore = 0;
//        isBig = false;
//
//        for (int i = 0; i < 4; i++) {
//            Gobblet gobblet = board.getFront(i, 3 - i);
//            if (gobblet != null) {
//                if (gobblet.getGobbletColor() == myColor) {
//                    myScore++;
//                    isBig = gobblet.getGobbletSize() == GobbletSize.SIZE_4;
//                    sizeScore += gobblet.getGobbletSize().ordinal() * 5;
//
//                } else {
//                    opponentNewScore++;
//                    myScore--;
//                    sizeScore -= gobblet.getGobbletSize().ordinal() * 5;
//
//                }
//            }
//        }
//        if(opponentNewScore == 3 && !isBig)
//            criticalPosition = true;
//
//        GobbletColor winner = board.isWinningState(playerColor);
//
//
//
//        if(winner != null && winner == playerColor){
//            myScore += (10000 * (depth + 1));
//        }else if (winner != null){
//            myScore -= (10000 * (depth + 1));
//        }else if (difficulty != Difficulty.EASY && criticalPosition){
//            myScore -= (10000 * (depth + 1));
////            myScore -= (10000);
//        }
//
//        board.setCriticalState(criticalPosition);
//
//        return myScore + sizeScore;
//    }


    public GameMove iterativeDeepening(Board board, Game.Turn turn, int maxDepth) {
        currentRoot = new TreeNode(null, true);
        for (int depth = 1; depth <= maxDepth; depth++) {
            alphaBeta(board, turn, Integer.MIN_VALUE, Integer.MAX_VALUE, depth, true, currentRoot);
            if (Game.isTurnTimeLimitExceeded() || board.isWinningState(gobbletColors[turn.ordinal()]) == gobbletColors[turn.ordinal()]) {
                if (bestMove == null){
                    ArrayList<GameMove> possibleMoves = generatePossibleMoves(board, turn);
                    bestMove = possibleMoves.get(0);
                }
                return this.bestMove;
            }
             //System.out.println("Depth in ITS: " + depth);
        }

        return this.bestMove;
    }


}







