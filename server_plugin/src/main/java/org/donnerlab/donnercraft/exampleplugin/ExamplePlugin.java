package org.donnerlab.donnercraft.exampleplugin;

import io.grpc.stub.StreamObserver;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


import lnrpc.Rpc.*;
import org.bukkit.util.Vector;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public final class ExamplePlugin extends JavaPlugin implements Listener {


    public LndRpc lndRpc;

    private Map<String, PlayerCommandPayload> commandPayloadMap;
    private List<SellOrder> sellOrders;
    @Override
    public void onEnable() {
        commandPayloadMap = new HashMap<>();
        sellOrders = new LinkedList<>();
        SetupRpc();
        getCommand("invoice").setExecutor(new CommandInvoice(this));
        getCommand("teleport").setExecutor(new CommandTeleport(this));
        getCommand("sell").setExecutor(new CommandSell(this));
        getCommand("pay").setExecutor(new CommandPay(this));
        getCommand("listsellorders").setExecutor(new CommandListSellOrders(this));
        getCommand("getsellorder").setExecutor(new CommandGetSellOrder(this));
        getCommand("claim").setExecutor(new CommandClaim(this));
        GetInfoResponse response = lndRpc.blockingStub.getInfo(GetInfoRequest.getDefaultInstance());
        System.out.println(response.getIdentityPubkey());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(player.getName() + " has joined the server :)");
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(player.getName() + " has quit the server :/");
    }

    public void SetupRpc() {
        lndRpc = new LndRpc();
        System.out.println(lndRpc.getPaymentRequest("test", 2));
        lndRpc.subscribeInvoices(new StreamObserver<Invoice>() {

            @Override
            public void onNext(Invoice invoice) {
                System.out.println("new Invoice: " + invoice.getRPreimage());
                if(commandPayloadMap.containsKey(invoice.getPaymentRequest())) {
                    System.out.println("found invoice");

                    switch(commandPayloadMap.get(invoice.getPaymentRequest()).commandType) {
                        case("teleport"):
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            p.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            Location loc = p.getLocation();
                            Vector dir = loc.getDirection();
                            dir.normalize();
                            dir.multiply((int)invoice.getValue() / 5); //5 blocks a way
                            loc.add(dir);
                            p.teleport(loc );
                            break;
                        case("sell"):
                            Player recipient = commandPayloadMap.get(invoice.getPaymentRequest()).recipient;
                            recipient.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            Player sender = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            sender.sendMessage(recipient.getDisplayName() + " paid your invoice");
                    }}
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });
    }

    public void AddTeleportRequest(int steps, Player p) {
        System.out.println("get teleport request for " + steps + " steps by "+  p.getName());
        String request = lndRpc.getPaymentRequest("teleport", steps*5);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p, map,"teleport");
        commandPayloadMap.put(request, payload);
        p.sendMessage(request);
    }

    public void AddPlayerPayRequest(Player sender, Player recipient, String payReq) {
        ItemStack map = QRMapSpawner.SpawnMap(recipient, payReq);
        PlayerCommandPayload payload = new PlayerCommandPayload(sender, map, "sell", recipient);
        commandPayloadMap.put(payReq, payload);
        recipient.sendMessage(payReq);

    }

    public void AddSellOrderRequest(Player sender, String payReq, ItemStack item) {
        sellOrders.add(new SellOrder(item, payReq));

        sender.getInventory().remove(item);

    }

    public void listSellOrders(Player sender){
        if(sellOrders.size() >= 0) {
            for (int i = 0; i < sellOrders.size(); i++) {
                SellOrder temp = sellOrders.get(i);
                if (!temp.claimed)
                 sender.sendMessage(i + " item: " + temp.item + " cost: " + lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getNumSatoshis());
            }
        }
    }

    public void buyItem(Player sender, int number) {

        SellOrder sellOrder = sellOrders.get(number);
        if (!sellOrder.claimed) {
            sender.sendMessage(sellOrder.payReq);
            QRMapSpawner.SpawnMap(sender, sellOrder.payReq);
        } else {
            sender.sendMessage("item already claimed");
        }


    }

    public void claimItem(Player sender, String preimage){
        ItemStack item = findMatch(preimage);
        if (item != null)
            sender.getInventory().addItem(item);
    }

    ItemStack findMatch(String preimage){
        try {
            String paymentHashIn = Sha.hash256(preimage);
            if(sellOrders.size() >= 0) {
                for (int i = 0; i < sellOrders.size(); i++) {
                    SellOrder temp = sellOrders.get(i);
                    String paymentHashCalc = lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getPaymentHash();

                    if( paymentHashCalc.equals(paymentHashIn)) {

                        ItemStack item = sellOrders.get(i).item;
                        sellOrders.get(i).claimed = true;
                        return item;

                    };
                }
            }
        } catch (Exception se) {
            System.out.println(se.getMessage());
        }

        return null;
    }


}
