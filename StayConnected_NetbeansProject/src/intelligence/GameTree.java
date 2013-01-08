
package intelligence;

import game.GameState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class GameTree {
    
    GameTreeNode root;
    
    public static int LEVELS = 3;
    
    public GameTree(GameState g) {
        root = new GameTreeNode(g, 0, GameState.COMP_COIN);        
        LinkedList<GameTreeNode> queue = new LinkedList<GameTreeNode>();
        queue.add(root);
        while(!queue.isEmpty()) {
            GameTreeNode node = queue.remove();
            if(node.getLevel() < LEVELS) {
                node.generateChildren();
                ArrayList<GameTreeNode> children = node.getChildren();
                try {
                    for(GameTreeNode gtn : children) {
                        queue.add(gtn);
                    }
                } catch(NullPointerException ex) {
                    continue;
                }
            }
        }
    }
    
    public GameState getNextGameState() {
        double maxWinFactor = -1;
        ArrayList<GameTreeNode> final_states = new ArrayList<GameTreeNode>();

        for(GameTreeNode gtn : root.getChildren()) {
            gtn.start();
        }        
        for(GameTreeNode gtn : root.getChildren()) {
            synchronized(this){
                try{
                    gtn.join();
                } catch(Exception e)
                {
                    System.out.println("Error : " + e);
                }
            }
            double factor = gtn.getWinFactor();
                    
            if(factor > maxWinFactor) {
                maxWinFactor = factor;
                final_states.clear();                        
                final_states.add(gtn);                
            } else if(factor == maxWinFactor) {
                final_states.add(gtn);
            }            
        }
        GameTreeNode final_state;
        if(final_states.size() > 1) {
            int i = (new Random()).nextInt(final_states.size());
            final_state = final_states.get(i);
        } else {
            final_state = final_states.get(0);
        }
        return final_state.getGameState();
    }
    
    public GameTreeNode getRoot() {
        return root;
    }   

}
