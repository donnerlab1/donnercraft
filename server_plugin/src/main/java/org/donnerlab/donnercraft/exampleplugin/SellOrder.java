package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.inventory.ItemStack;

public class SellOrder {
    public String payReq;
    public ItemStack item;
    public boolean claimed;

    public SellOrder(ItemStack item, String payreq) {
        this.payReq = payreq;
        this.item = item;
        claimed = false;
    }

    public void claim() {
        claimed = true;
    }
}
