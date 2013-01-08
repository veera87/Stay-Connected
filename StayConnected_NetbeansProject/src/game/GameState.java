
package game;

import java.util.LinkedList;


public class GameState {
    private int board[][];
    private int board_size;
    public static final int EMPTY = 0;
    public static final int USER_COIN = 1;
    public static final int COMP_COIN = 2;
    
    public static final int USER_WON = 1;
    public static final int COMP_WON = 2;
    public static final int DRAW = 3;
    public static final int NO_WIN = 4;
    
    public static final int NORTH = 10;
    public static final int EAST = 21;
    public static final int WEST = 1;
    public static final int SOUTH = 12;
    public static final int NORTH_EAST = 20;
    public static final int NORTH_WEST = 22;
    public static final int SOUTH_EAST = 0;
    public static final int SOUTH_WEST = 2;
        
    public GameState(int board_size) {        
        this.board_size = board_size;
        board = new int[board_size][board_size];
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                board[i][j] = EMPTY;
            }
        }
        for(int i = 1; i < board_size - 1; i++) {
            board[0][i] = USER_COIN;
            board[board_size - 1][i] = USER_COIN;
            board[i][0] = COMP_COIN;
            board[i][board_size - 1] = COMP_COIN;
        }        
    }
    
    public GameState(GameState gs1) {
        this.board_size = gs1.getBoardSize();
        board = new int[board_size][board_size];
        for(int i = 0; i < board_size; i++) {
            for(int j = 0; j < board_size; j++) {
                board[i][j] = gs1.getCoinAt(i, j);
            }
        }
    }
    
    public int getBoardSize() {
        return board_size;
    }
    
    public int getCoinAt(int i, int j) {
        return board[i][j];
    }
    
    public boolean move(int x, int y, int direction) {
        int coin_type = board[x][y];
        if(coin_type == EMPTY)
            return false;
        
        int x_inc = (direction / 10) - 1;
        int y_inc = (direction % 10) - 1;
        
        int x_curr = x + x_inc;
        int y_curr = y + y_inc;
        int move_distance = 1;
        /*Calculates the distance the coin can make in that direction by 
         calculating number of coins in the path*/
        while(true) { 
            try {
                if (board[x_curr][y_curr] != EMPTY) {
                    move_distance++;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                break;
            }
            x_curr += x_inc;
            y_curr += y_inc;
        }
        
        x_curr = x - x_inc;
        y_curr = y - y_inc;
        
        while(true) { 
            try {
                if (board[x_curr][y_curr] != EMPTY) {
                    move_distance++;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                break;
            }
            x_curr -= x_inc;
            y_curr -= y_inc;
        }
        
        //checks if the opponent's coin is present in its path
        x_curr = x + x_inc;
        y_curr = y + y_inc;        
        for(int i = 1; i < move_distance; i++) {            
            try {
                if (board[x_curr][y_curr] != coin_type && board[x_curr][y_curr] != EMPTY) {
                    return false;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                return false;
            }
            x_curr += x_inc;
            y_curr += y_inc;
        }
        //checks if same type of coin is present at the destinations
        try {
            if (board[x_curr][y_curr] == coin_type) {
                return false;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
        //making the move
        board[x][y] = EMPTY;
        board[x_curr][y_curr] = coin_type;
        return true;
    }
    
    public int whoWon() {
        int i = 0, j = 0;
        for(i = 0; i < board_size; i++) {
            for(j = 0; j < board_size; j++) {
                if(board[i][j] == USER_COIN)
                    break;
            }
            if(j < board_size) {
                break;
            }
        }
        boolean user_won = check_win(i, j);                
        
        for(i = 0; i < board_size; i++) {
            for(j = 0; j < board_size; j++) {
                if(board[i][j] == COMP_COIN)
                    break;
            }
            if(j < board_size) {
                break;                
            }                
        }
        boolean comp_won = check_win(i, j);
                
        if(user_won && comp_won)
            return DRAW;
        else if(user_won)
            return USER_WON;
        else if(comp_won)
            return COMP_WON;
        else
            return NO_WIN;            
    }
    
    private boolean check_win(int x, int y) {
        boolean visited[][] = new boolean[board_size][board_size];
        int coin_type = board[x][y];
        int total_coins = 0;
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                visited[i][j] = false;
                if (board[i][j] == coin_type) {
                    total_coins++;
                }
            }
        }        
        LinkedList<Integer> queue = new LinkedList<Integer>();
        int count = 0;
        int x_curr = x;
        int y_curr = y;
        queue.add((x_curr * 10) + y_curr);
        while (!queue.isEmpty()) {
            int val = queue.remove();
            x_curr = val / 10;
            y_curr = val % 10;
            visited[x_curr][y_curr] = true;
            count++;
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    try {
                        if (board[x_curr + i][y_curr + j] == coin_type && visited[x_curr + i][y_curr + j] == false) {
                            if(!queue.contains((x_curr + i) * 10 + (y_curr + j)))
                            queue.add((x_curr + i) * 10 + (y_curr + j));
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        continue;
                    }
                }
            }            
        }
        if(count == total_coins)
            return true;
        return false;        
    }
}