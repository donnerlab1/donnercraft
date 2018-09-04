package org.donnerlab.donnercraft;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class SellOrder {
    public String payReq;
    public ItemStack item;
    public boolean claimed;
    public boolean isPublic;
    public Player player;

    public SellOrder(ItemStack item, String payreq, Player player) {
        this.payReq = payreq;
        this.item = item;
        this.claimed = false;
        this.isPublic = true;
        this.player = player;
    }
    public SellOrder(ItemStack item, String payreq, boolean isPublic, Player player) {
        this.payReq = payreq;
        this.item = item;
        claimed = false;
        this.isPublic = isPublic;
        this.player = player;
    }

    public void claim() {
        this.claimed = true;
    }
}
