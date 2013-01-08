
package utils;

import game.GameState;
import java.util.ArrayList;

public class Utils {

    static class CoinNode {

        int x, y;
        boolean visited;

        CoinNode(int a, int b) {
            x = a;
            y = b;
            visited = false;
        }

        int distance(CoinNode a) {
            return Math.max(Math.abs(x - a.x), Math.abs(y - a.y));
        }
    }

    public static int prims(GameState gs, int cointype) {

        ArrayList<CoinNode> nodes = convert2CoinNodeList(gs, cointype);

        int total_distance = 0;
        int curr_distance = 1000;
        CoinNode curr_closest = null;
        try {
            nodes.get(0).visited = true;
        } catch (IndexOutOfBoundsException ex) {
            return 0;
        }
        int visited_count = 1;
        int max_distance = 0;
        while (visited_count < nodes.size()) {
            for (CoinNode cn : nodes) {
                if (cn.visited) {
                    for (CoinNode cn1 : nodes) {
                        if (!cn1.visited) {
                            int new_dist = cn.distance(cn1);
                            if (new_dist < curr_distance) {
                                curr_distance = new_dist;
                                curr_closest = cn1;
                            }
                        }
                    }
                }
            }
            curr_closest.visited = true;
            total_distance += curr_distance;
            if (curr_distance > max_distance) {
                max_distance = curr_distance;
            }
            curr_distance = 1000;
            visited_count++;
        }
        return total_distance * max_distance;

    }
    
     private static ArrayList<CoinNode> convert2CoinNodeList(GameState gs, int cointype) {
        ArrayList<CoinNode> nodes = new ArrayList<Utils.CoinNode>();
        for (int i = 0; i < gs.getBoardSize(); i++) {
            for (int j = 0; j < gs.getBoardSize(); j++) {
                if (gs.getCoinAt(i, j) == cointype) {
                    nodes.add(new CoinNode(i, j));
                }
            }
        }
        return nodes;
    }  
    
}


