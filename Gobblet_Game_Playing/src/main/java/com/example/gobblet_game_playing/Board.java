package com.example.gobblet_game_playing;

import java.util.Stack;

import static com.example.gobblet_game_playing.GobbletColor.*;

public class Board {

    private Stack<Gobblet>[][] board;                   /*2D array of Gobblet stack to represent the board*/
    private Stack<Gobblet>[][] playersGobblets;         /*2 arrays of Gobblet stack to represent the gobblets stacks of each player*/

    /**
     * Board
     * Constructor
     */
    public Board() {

        GobbletColor[] gobbletColors = GobbletColor.values();

        board = new Stack[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                board[i][j] = new Stack<>();
            }
        }

        playersGobblets = new Stack[2][3];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                this.playersGobblets[i][j] = new Stack<>();
                this.playersGobblets[i][j].push(new Gobblet(gobbletColors[i], GobbletSize.SIZE_1, -1, -1));
                this.playersGobblets[i][j].push(new Gobblet(gobbletColors[i], GobbletSize.SIZE_2, -1, -1));
                this.playersGobblets[i][j].push(new Gobblet(gobbletColors[i], GobbletSize.SIZE_3, -1, -1));
                this.playersGobblets[i][j].push(new Gobblet(gobbletColors[i], GobbletSize.SIZE_4, -1, -1));
            }
        }
    }

    /**
     * Board
     * Copy constructor
     */
    public Board(Board board) {

        this.board = new Stack[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.board[i][j] = new Stack<>();
                for (int z = 0; z < board.getBoard()[i][j].size(); z++) {
                    Gobblet gobblet = new Gobblet(board.getBoard()[i][j].elementAt(z));
                    this.board[i][j].push(gobblet);
                }
            }
        }
        this.playersGobblets = new Stack[2][3];
        this.playersGobblets[0] = new Stack[3];
        this.playersGobblets[1] = new Stack[3];
        for (int i = 0; i < 3; i++) {
            this.playersGobblets[0][i] = new Stack<>();
            for (int j = 0; j < board.getPlayersGobblets()[0][i].size(); j++) {
                Gobblet gobblet = new Gobblet(board.getPlayersGobblets()[0][i].elementAt(j));
                this.playersGobblets[0][i].push(gobblet);
            }

            this.playersGobblets[1][i] = new Stack<>();
            for (int j = 0; j < board.getPlayersGobblets()[1][i].size(); j++) {
                Gobblet gobblet = new Gobblet(board.getPlayersGobblets()[1][i].elementAt(j));
                this.playersGobblets[1][i].push(gobblet);
            }
        }
    }

    /**
     * isWinningState
     * Check if there's a winner
     * @return
     * the corresponding GobbletColor, if there's a winner
     * null otherwise
     */
    GobbletColor isWinningState(GobbletColor myGobbletColor){
        GobbletColor opponentGobbletColor = (myGobbletColor == WHITE)? BLACK:WHITE;
        boolean winFlag = false;
        boolean loseFlag = false;
        int myScore;
        int opponentScore;

        //Check Rows
        for(int i = 0; i < 4; i++) {
            myScore = 4;
            opponentScore = 4;
            for (int j = 0; j < 4; j++) {
                if (this.getFront(i, j) != null){
                    if(this.getFront(i, j).getGobbletColor() == myGobbletColor) {
                        myScore--;
                    }else if(this.getFront(i, j).getGobbletColor() == opponentGobbletColor){
                        opponentScore--;
                    }
                }
            }

            if(myScore == 0)
                winFlag = true;
            if(opponentScore == 0)
                loseFlag = true;
        }

        if(winFlag && loseFlag){
            return opponentGobbletColor;
        }else if(loseFlag){
            return opponentGobbletColor;
        } else if (winFlag) {
            return  myGobbletColor;
        }

        //Check Columns
        for(int j = 0; j < 4; j++) {
            myScore = 4;
            opponentScore = 4;
            for (int i = 0; i < 4; i++) {
                if (this.getFront(i, j) != null){
                    if(this.getFront(i, j).getGobbletColor() == myGobbletColor) {
                        myScore--;
                    }else if(this.getFront(i, j).getGobbletColor() == opponentGobbletColor){
                        opponentScore--;
                    }
                }
            }

            if(myScore == 0)
                winFlag = true;
            if(opponentScore == 0)
                loseFlag = true;

        }

        if(winFlag && loseFlag){
            return opponentGobbletColor;
        }else if(loseFlag){
            return opponentGobbletColor;
        } else if (winFlag) {
            return  myGobbletColor;
        }

        //Check Diagonals
        myScore = 4;
        opponentScore = 4;
        for (int i = 0; i < 4; i++) {
            if (this.getFront(i, i) != null){
                if(this.getFront(i, i).getGobbletColor() == myGobbletColor) {
                    myScore--;
                }else if(this.getFront(i, i).getGobbletColor() == opponentGobbletColor){
                    opponentScore--;
                }
            }
        }

        if(myScore == 0)
            winFlag = true;
        if(opponentScore == 0)
            loseFlag = true;


        myScore = 4;
        opponentScore = 4;
        for (int i = 0; i < 4; i++) {
            if (this.getFront(i, 3 - i) != null){
                if(this.getFront(i, 3 - i).getGobbletColor() == myGobbletColor) {
                    myScore--;
                }else if(this.getFront(i, 3 - i).getGobbletColor() == opponentGobbletColor){
                    opponentScore--;
                }
            }
        }

        if(myScore == 0)
            winFlag = true;
        if(opponentScore == 0)
            loseFlag = true;


        if(winFlag && loseFlag){
            return opponentGobbletColor;
        }else if(loseFlag){
            return opponentGobbletColor;
        } else if (winFlag) {
            return  myGobbletColor;
        }

        return null;
    }

    /**
     * PlayRound
     * edit the 2d array in the board
     * move the gobblet from x1,y1 to x2,y2
     * -1,-1 means the gobblet is off board
     * Pop from the gobblet stacks
     */
    void playRound(GameMove move, Game.Turn currentTurn) {
        // boolean onBoard = false;
        Gobblet gobblet = move.getGobblet();
        int x1 = gobblet.getX();
        int y1 = gobblet.getY();
        int x2 = move.getX();
        int y2 = move.getY();

        /* If moving an existing gobblet, pop it from the original position */
        if (x1 != -1 && y1 != -1) {
            // onBoard = true;
            if (getFront(x1, y1) == null) {
                System.out.println("null");
            }
            gobblet = board[x1][y1].pop();

        } else {
            playersGobblets[currentTurn.ordinal()][move.getStackNo()].pop();
        }

        /* Place the gobblet at the new position */
        gobblet.setX(x2);
        gobblet.setY(y2);
        board[x2][y2].push(gobblet);
        Game.turnStartTime =  System.currentTimeMillis();

    }

    /**
     * getFront
     * @return the peek front gobblet at certain position on board
     */
    Gobblet getFront(int x, int y) {
        if (x >= 0 && x < 4 && y >= 0 && y < 4) {
            if (board[x][y].isEmpty()) {
                return null;
            } else {
                return board[x][y].peek();
            }
        } else {
            System.out.println("Board: Invalid Position");
            return null;
        }
    }

    /**
     * printBoard
     * Display the board and the gobblet stacks for testing
     */
    void printBoard() {

        for (int i = 0; i < board.length; i++) {

            if (i < 3 && !playersGobblets[0][i].isEmpty() && playersGobblets[0][i].peek().getGobbletColor().ordinal() == 0) {
                System.out.print("w" + playersGobblets[0][i].peek().getGobbletSize().ordinal() + "    ");
            } else {
                System.out.print("      ");
            }

            for (int j = 0; j < board.length; j++) {
                if (!board[i][j].isEmpty())
                    if (board[i][j].peek().getGobbletColor().ordinal() == WHITE.ordinal()) {
                        System.out.print("w" + board[i][j].peek().getGobbletSize().ordinal() + " ");

                    } else {
                        System.out.print("b" + board[i][j].peek().getGobbletSize().ordinal() + " ");
                    }
                else {
                    System.out.print("-1 ");
                }
            }

            if (i < 3 && !playersGobblets[1][i].isEmpty() && playersGobblets[1][i].peek().getGobbletColor().ordinal() == 1) {
                System.out.print("    b" + playersGobblets[1][i].peek().getGobbletSize().ordinal());
            } else {
                System.out.print("  ");
            }

            System.out.println();

        }
    }

    public Stack<Gobblet>[][] getBoard() {
        return board;
    }

    public Stack<Gobblet>[][] getPlayersGobblets() {
        return playersGobblets;
    }

}