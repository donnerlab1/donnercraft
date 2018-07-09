package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerCommandPayload {

    public Player sender;
    public Player recipient;
    public String commandType;
    public ItemStack qrMap;

    public PlayerCommandPayload(Player sender, ItemStack qrMap, String commandType){
        this.sender = sender;
        this.qrMap = qrMap;
        this.commandType = commandType;
    }
    public PlayerCommandPayload(Player sender, ItemStack qrMap, String commandType, Player recipient){
        this.sender = sender;
        this.recipient = recipient;
        this.qrMap = qrMap;
        this.commandType = commandType;
    }

}


