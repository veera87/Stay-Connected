
package intelligence;

import game.GameState;
import java.util.ArrayList;
import utils.Utils;

public class GameTreeNode extends Thread{
    
    private GameState gs;
    private ArrayList<GameTreeNode> children;
    private int level, coinType;
    private double winFactor;
    
    @Override
    public void run()
    {
        calculateWinFactor();
        synchronized(this)
        {
            try{
                this.notify();
            }
            catch(Exception e)
            {
                System.out.println("Error : " + e);
            }
        }
    }
    
    public GameTreeNode(GameState g, int l, int cointype) {
        gs = g;
        level = l;
        coinType = cointype;
        children = null;
    }
    
    public int getLevel() {
        return level;
    }
    
    public GameState getGameState() {
        return gs;
    }
    
    public void generateChildren() {        
        if(gs.whoWon() == GameState.NO_WIN) {
            children = new ArrayList<GameTreeNode>();
            for(int i = 0; i < gs.getBoardSize(); i++) {
                for(int j = 0; j < gs.getBoardSize(); j++) {
                    if(gs.getCoinAt(i, j) == coinType) {
                        for(int k = 0; k < 3; k++) {
                            for(int l = 0; l < 3; l++) {
                                if(l != 1 || k != 1)  {
                                    GameState gs_temp = new GameState(gs);
                                    if(gs_temp.move(i, j, (k * 10) + l)) {
                                        children.add(new GameTreeNode(gs_temp, level + 1, (coinType == GameState.COMP_COIN) ? GameState.USER_COIN : GameState.COMP_COIN));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }        
    }
    
    public ArrayList<GameTreeNode> getChildren() {
        return children;
    }
    
    public double calculateWinFactor() {
        if (children == null || children.size() == 0) {
            if (gs.whoWon() == GameState.NO_WIN) {
                int comp_proximity = Utils.prims(gs, GameState.COMP_COIN);
                int user_proximity = Utils.prims(gs, GameState.USER_COIN);
                winFactor = (double) (user_proximity - comp_proximity) / Math.max(comp_proximity, user_proximity);
            } else if (gs.whoWon() == GameState.COMP_WON) {
                winFactor = 1.0;
            } else if(gs.whoWon() == GameState.USER_WON) {
                winFactor = -1.0;
            } else if(gs.whoWon() == GameState.DRAW) {
                winFactor = 0.99;
            }
        } else {
            double totalWinFactor = 0;
            for(GameTreeNode gtn : children) {
                totalWinFactor += gtn.calculateWinFactor();
            }            
            winFactor = totalWinFactor / children.size();            
        }        
        return getWinFactor();
    }    

    public double getWinFactor() {
        return winFactor;
    }
}