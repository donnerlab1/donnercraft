package org.donnerlab.donnercraft;

import org.bukkit.inventory.ItemStack;

public class SellOrder {
    public String payReq;
    public ItemStack item;
    public boolean claimed;
    public boolean isPublic;

    public SellOrder(ItemStack item, String payreq) {
        this.payReq = payreq;
        this.item = item;
        this.claimed = false;
        this.isPublic = true;
    }
    public SellOrder(ItemStack item, String payreq, boolean isPublic) {
        this.payReq = payreq;
        this.item = item;
        claimed = false;
        this.isPublic = isPublic;
    }

    public void claim() {
        this.claimed = true;
    }
}
