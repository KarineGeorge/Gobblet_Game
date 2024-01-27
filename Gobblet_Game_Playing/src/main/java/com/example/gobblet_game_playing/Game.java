package com.example.gobblet_game_playing;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Game {

    private Board board;                                  /*the game board*/
    private Player winner;                                /*the winner player*/
    private PlayerPair players;                           /*the two players*/
    public Turn currentTurn;                              /*the current turn*/
    static long turnStartTime =0 ;                        /*this turn start time*/
    static long turnTimeLimitMillis = 10000;              /*the turn time limit*/
    private GobbletColor[] gobbletColors = GobbletColor.values();
    private static ObjectProperty<Turn> currentTurnProperty = new SimpleObjectProperty<>(Turn.A);

    public static ObjectProperty<Turn> currentTurnProperty() {
        return currentTurnProperty;
    }

    public enum Turn{
        A,
        B
    };

    public enum Difficulty{
        EASY,
        NORMAL,
        HARD
    };

    public enum PlayerType{
        HUMAN,
        COMPUTER
    };

    /**
     * Game Constructor
     * it creates a Board object and the 2 players objects
     */
    public Game(PlayerType t1, String name1, Difficulty gameDifficulty1, PlayerType t2, String name2, Difficulty gameDifficulty2) {
        board = new Board();
        Player p1 = null;
        if(t1 == PlayerType.HUMAN){
            p1 = new HumanPlayer(name1, GobbletColor.WHITE);
        }else if(t1 == PlayerType.COMPUTER){
            p1 = new ComputerPlayer(name1, GobbletColor.WHITE, gameDifficulty1);
        }

        Player p2 = null;
        if(t2 == PlayerType.HUMAN){
            p2 = new HumanPlayer(name2, GobbletColor.BLACK);
        }else if(t2 == PlayerType.COMPUTER){
            p2 = new ComputerPlayer(name2, GobbletColor.BLACK, gameDifficulty2);
        }
        if(p1 != null && p2 != null){
            players = new PlayerPair(p1, p2);
        }else{
            System.out.println("ERROR: fail to create the players");
        }

        if(t1 == PlayerType.COMPUTER && t2 == PlayerType.COMPUTER){
            if(gameDifficulty1 == Difficulty.HARD && gameDifficulty2 == Difficulty.HARD){
                ((ComputerPlayer) players.getPlayer1()).setSearchDepth(4);
                ((ComputerPlayer) players.getPlayer2()).setSearchDepth(4);
            }else if(gameDifficulty1 == Difficulty.NORMAL && gameDifficulty2 ==Difficulty.HARD){
                ((ComputerPlayer) players.getPlayer1()).setSearchDepth(2);
                ((ComputerPlayer) players.getPlayer2()).setSearchDepth(4);
            }else if(gameDifficulty1 == Difficulty.HARD && gameDifficulty2 == Difficulty.NORMAL){
                ((ComputerPlayer) players.getPlayer1()).setSearchDepth(4);
                ((ComputerPlayer) players.getPlayer2()).setSearchDepth(2);
            }
        }
        currentTurn = Turn.A;
    }

    /**
     * switch turns between A and B
     */
    public void switchTurn() {
        currentTurn = (currentTurn == Turn.A) ? Turn.B : Turn.A;
    }


    /**
     * isGameEnded
     * check if game ends by winning
     * @return boolean
     */
    public boolean isGameEnded() {

        if(board.isWinningState(gobbletColors[(currentTurn.ordinal()+1)%2]) == GobbletColor.WHITE){
            winner = players.getPlayer1();
            return true;
        }else if(board.isWinningState(gobbletColors[(currentTurn.ordinal()+1)%2]) == GobbletColor.BLACK){
            winner = players.getPlayer2();
            return true;
        }else{
            return false;
        }
    }

    /**
     * isTurnTimeLimitExceeded
     * Check if the current turn has exceeded the time limit
     * @return boolean
     */
    public static boolean isTurnTimeLimitExceeded() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - turnStartTime) > turnTimeLimitMillis;
    }

    /**
     * setCurrentGameMove
     * for HumanPlayer turn it receive a move and test it
     * @return true if the move was played successfully
     */
    public boolean setCurrentGameMove(int x1, int y1, int x2, int y2, int stackNo) {

        Gobblet gobblet;
        if(x1 == -1 && y1 == -1){
            if(currentTurn.ordinal() == 0){
                gobblet = board.getPlayersGobblets()[0][stackNo].peek();
            }else{
                gobblet = board.getPlayersGobblets()[1][stackNo].peek();
            }
        }else if(!board.getBoard()[x1][y1].isEmpty()){
            gobblet = board.getBoard()[x1][y1].peek();
        }else{
            return false;
        }

        GameMove move = new GameMove(gobblet, x2, y2, stackNo);

        if(currentTurn.ordinal() == 0){
            return ((HumanPlayer) players.getPlayer1()).playGobbletMove(move, board);

        }else{
            return ((HumanPlayer) players.getPlayer2()).playGobbletMove(move, board);
        }
    }

    /**
     * getComputerMove
     * for the ComputerPlayer to get the next played move and play it on the board
     */
    public GameMove getComputerMove(){
        if(currentTurn.ordinal()==0){
            GameMove move = ((ComputerPlayer) players.getPlayer1()).playGobbletMove(this.board,currentTurn);
            GameMove uncorruptedMove = new GameMove(move);
            board.playRound(move, currentTurn);
            return uncorruptedMove;
        }else{
            GameMove move = ((ComputerPlayer) players.getPlayer2()).playGobbletMove(this.board,currentTurn);
            GameMove uncorruptedMove = new GameMove(move);
            board.playRound(move, currentTurn);
            return uncorruptedMove;
        }
    }

    /**
     *  Set the time limit for each turn
     * @param seconds
     */
    public void setTurnTimeLimit(int seconds) {
        turnTimeLimitMillis = seconds * 1000L; // Convert seconds to milliseconds
    }

    /**
     *  setters and getters
     */
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public PlayerPair getPlayers() {
        return players;
    }

    public void setPlayers(PlayerPair pair) {
        this.players = pair;
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public static void setCurrentTurn(Turn turn) {
        currentTurnProperty.set(turn);
    }
    public void setTurnStartTime(long turnStartTime) {
        this.turnStartTime = turnStartTime;
    }
}
