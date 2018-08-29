package org.donnerlab.donnercraft;

import java.util.List;
import org.bukkit.entity.Player;
public class PlayerInfo {

    public Player player;
    public String pubkey;
    public String hostname;
    public String ip;
    public List<SellOrder> sellOrders;

    public PlayerInfo(Player player) {this.player = player;}
}
