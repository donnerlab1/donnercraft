package org.donnerlab.donnercraft;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SellOrder  implements ConfigurationSerializable
{
    {
        ConfigurationSerialization.registerClass(SellOrder.class);
    }
    public String payReq;
    public ItemStack item;
    public boolean claimed;
    public boolean isPublic;
    public String player;

    public SellOrder(ItemStack item, String payreq, Player player) {
        this.payReq = payreq;
        this.item = item;
        this.claimed = false;
        this.isPublic = true;
        this.player = player.getDisplayName();
    }
    public SellOrder(ItemStack item, String payreq, boolean isPublic, Player player) {
        this.payReq = payreq;
        this.item = item;
        claimed = false;
        this.isPublic = isPublic;
        this.player = player.getDisplayName();
    }
    public SellOrder(){}
    public void claim() {
        this.claimed = true;
    }


    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("payReq", payReq);
        map.put("item", item);
        map.put("claimed",claimed);
        map.put("isPublic",isPublic);
        map.put("player",player);
        return map;

    }

    public static SellOrder deserialize(Map<String, Object> map) {
        SellOrder tmp = new SellOrder();
        tmp.payReq = (String) map.get("payReq");
        tmp.item = (ItemStack) map.get("item");
        tmp.claimed = (boolean) map.get("claimed");
        tmp.isPublic = (boolean) map.get("isPublic");
        tmp.player = (String) map.get("player");
        return tmp;
    }
}
