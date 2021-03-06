package org.donnerlab.donnercraft;


import io.grpc.stub.StreamObserver;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


import lnrpc.Rpc.*;
import org.bukkit.util.Vector;
import org.donnerlab.donnercraft.Commands.*;
import org.donnerlab.donnercraft.Utility.Sha;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public final class DonnerCraftPlugin extends JavaPlugin implements Listener {


    public LndRpc lndRpc;

    private Map<String, PlayerCommandPayload> commandPayloadMap;
    private Map<String,String> partialInvoiceMap;
    //private List<SellOrder> sellOrders;
    private Map<String,PlayerInfo> registeredPlayers;


    File homeFile;
    File lndFile;
    File sellOrderFile;
    FileConfiguration homeCfg;
    FileConfiguration lndCfg;
    FileConfiguration sellOrderCfg;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(SellOrder.class);
        commandPayloadMap = new HashMap<>();
        //sellOrders = new LinkedList<>();
        registeredPlayers = new HashMap<>();
        partialInvoiceMap = new HashMap<>();

        homeFile = new File("plugins/Donnercraft","homes.yml");
        homeCfg = YamlConfiguration.loadConfiguration(homeFile);
        homeCfg.set("test" + "." + "test2"+".world",0);

        lndFile = new File("plugins/Donnercraft","lnd.yml");
        lndCfg = YamlConfiguration.loadConfiguration(lndFile);

        sellOrderFile = new File("plugins/Donnercraft","sellorders.yml");
        sellOrderCfg = YamlConfiguration.loadConfiguration(sellOrderFile);

        setupRpc();
        setupCommands();
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

    private void setupRpc() {
        try {
            lndCfg.load(lndFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println(lndCfg.getString("rpc.hostname") + " " + lndCfg.getInt("rpc.port"));
        lndRpc = new LndRpc(lndCfg.getString("rpc.hostname"),lndCfg.getInt("rpc.port"));
        System.out.println(lndRpc.getPaymentRequest("test", 2));
        lndRpc.subscribeInvoices(new StreamObserver<Invoice>() {

            @Override
            public void onNext(Invoice invoice) {
                System.out.println("new Invoice: " + invoice.getRPreimage());
                if(commandPayloadMap.containsKey(invoice.getPaymentRequest())) {
                    System.out.println("found invoice");

                    switch(commandPayloadMap.get(invoice.getPaymentRequest()).commandType) {
                        case("teleport"): {
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            p.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            Location loc = p.getLocation();
                            Vector dir = loc.getDirection();
                            dir.normalize();
                            dir.multiply((int) invoice.getValue() / 5); //5 blocks a way
                            loc.add(dir);
                            p.teleport(loc);
                            break;
                        }
                        case("sell"): {
                            Player recipient = commandPayloadMap.get(invoice.getPaymentRequest()).recipient;
                            recipient.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            Player sender = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            sender.sendMessage(recipient.getDisplayName() + " paid your invoice");
                            break;
                        }
                        case("register"): {
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            registeredPlayers.put(p.getDisplayName(),new PlayerInfo(p));
                            p.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            p.sendMessage("successfully registerd");
                            break;
                        }
                        case("sethome"): {
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            p.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            String[] parts = invoice.getMemo().split(";");
                            homeCfg.set(parts[1] + "." + parts[2]+".world",parts[3]);
                            homeCfg.set(parts[1] + "." + parts[2]+".x",Double.parseDouble(parts[4]));
                            homeCfg.set(parts[1] + "." + parts[2]+".y",Double.parseDouble(parts[5]));
                            homeCfg.set(parts[1] + "." + parts[2]+".z",Double.parseDouble(parts[6]));
                            homeCfg.set(parts[1] + "." + parts[2]+".yaw",Double.parseDouble(parts[7]));
                            homeCfg.set(parts[1] + "." + parts[2]+".pitch",Double.parseDouble(parts[8]));
                            try{
                                homeCfg.save(homeFile);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            p.sendMessage("home "+parts[2]+" saved");
                            break;
                        }
                        case("home"): {
                            try {
                                homeCfg.load(homeFile);
                            } catch (IOException | InvalidConfigurationException e) {
                                e.printStackTrace();
                            }
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            p.getInventory().remove(commandPayloadMap.get(invoice.getPaymentRequest()).qrMap);
                            String[] parts = invoice.getMemo().split(";");
                            String world = homeCfg.getString(parts[1]+"."+parts[2]+".world");
                            double x = homeCfg.getDouble(parts[1]+"."+parts[2]+".x");
                            double y = homeCfg.getDouble(parts[1]+"."+parts[2]+".y");
                            double z = homeCfg.getDouble(parts[1]+"."+parts[2]+".z");
                            double yaw = homeCfg.getDouble(parts[1]+"."+parts[2]+".yaw");
                            double pitch = homeCfg.getDouble(parts[1]+"."+parts[2]+".pitch");

                            Location loc = new Location(Bukkit.getWorld(world), x, y,z);
                            loc.setPitch((float) pitch);
                            loc.setYaw((float) yaw);
                            p.teleport(loc);
                            p.sendMessage("teleported to " + parts[2]);
                            break;
                        }
                        case("emerald"): {
                            Player p = commandPayloadMap.get(invoice.getPaymentRequest()).sender;
                            ItemStack emeralds = new ItemStack(Material.EMERALD);
                            emeralds.setAmount((int)invoice.getValue()/100);
                            p.getInventory().addItem(emeralds);
                        }
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

   private void setupCommands() {

        //getCommand("invoice").setExecutor(new CommandInvoice(this));
        getCommand("teleport").setExecutor(new CommandTeleport(this));
        getCommand("sell").setExecutor(new CommandSell(this));
        //getCommand("pay").setExecutor(new CommandPay(this));
        getCommand("listsellorders").setExecutor(new CommandListSellOrders(this));
        //getCommand("ls").setExecutor(new CommandListSellOrders(this));
        getCommand("getsellorder").setExecutor(new CommandGetSellOrder(this));
        getCommand("claim").setExecutor(new CommandClaim(this));
        //getCommand("register").setExecutor(new CommandRegister(this));
       getCommand("home").setExecutor(new CommandHome(this));
       getCommand("sethome").setExecutor(new CommandSetHome(this));

       getCommand("channel").setExecutor(new CommandChannel(this));
       getCommand("removesellorders").setExecutor(new CommandRemoveSellOrders(this));
       getCommand("emerald").setExecutor(new CommandEmerald(this));
    }

    public void AddEmeraldRequest(Player p, int count) {
        String request = lndRpc.getPaymentRequest("emerald", count*100);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p,map,"emerald");
        commandPayloadMap.put(request,payload);
        p.sendMessage(request);
    }
    public void AddTeleportRequest(int steps, Player p) {
        System.out.println("get teleport request for " + steps + " steps by "+  p.getName());
        String request = lndRpc.getPaymentRequest("teleport", steps*5);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p, map,"teleport");
        commandPayloadMap.put(request, payload);
        p.sendMessage(request);
    }
    public void AddRegisterRequest(Player p) {
        String request = lndRpc.getPaymentRequest("register;"+p.getName(), 1);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p, map,"register");
        commandPayloadMap.put(request, payload);
        p.sendMessage(request);
    }

    public void AddSetHomeRequest(Player p,String homename, String world, double x, double y, double z, float pitch, float yaw) {
        try {
            homeCfg.load(homeFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if(homeCfg.isSet(p.getName()+"."+homename+".world")) {
            p.sendMessage("the home already exists");
            return;
        }
        String request = lndRpc.getPaymentRequest("sethome;"+p.getName()+";"+homename+";"+world+";"+x+";"+y+";"+z+";"+pitch+";"+yaw, 50);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p,map,"sethome");
        commandPayloadMap.put(request,payload);
        p.sendMessage(request);
    }

    public void AddHomeRequest(Player p, String homename) {
        try {
            homeCfg.load(homeFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        if(!homeCfg.isSet(p.getName()+"."+homename+".world")) {
            p.sendMessage("the home does not exist");
            return;
        }
        String request = lndRpc.getPaymentRequest("home;"+p.getName()+";"+homename,10);
        ItemStack map = QRMapSpawner.SpawnMap(p, request);
        PlayerCommandPayload payload = new PlayerCommandPayload(p,map,"home");
        commandPayloadMap.put(request,payload);
        p.sendMessage(request);
    }
    public void AddPubkeyRequest(Player p, String pubkey) {
        registeredPlayers.get(p.getDisplayName()).pubkey = pubkey;
    }
    public void AddChannelRequest(Player p) {
        String content="";
        content+=lndRpc.blockingStub.getInfo(GetInfoRequest.getDefaultInstance()).getIdentityPubkey();
        content+="@"+lndCfg.getString("info.externalip")+":"+lndCfg.getString("info.externalport");
        QRMapSpawner.SpawnMap(p,content);
        p.sendMessage(content);
    }
    public void AddPlayerPayRequest(Player sender, Player recipient, String payReq) {
        ItemStack map = QRMapSpawner.SpawnMap(recipient, payReq);
        PlayerCommandPayload payload = new PlayerCommandPayload(sender, map, "sell", recipient);
        commandPayloadMap.put(payReq, payload);
        recipient.sendMessage(payReq);

    }


    public void DBAddSellOrder(Player sender, String payReq, ItemStack item) {
        if(!checksellorder(payReq)) {
            sender.sendMessage("§c your invoice was wrong or to long. try splitting the invoice with /sell 1 'first part' and /sell 2 'second part'");
            return;
        }
        SellOrder so = new SellOrder(item, payReq, sender);
        sellOrderCfg.set(so.payReq, so);
        try{
            sellOrderCfg.save(sellOrderFile);
            sender.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

            sender.sendMessage(ChatColor.GREEN + " your sell order was added");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void AddSellOrderRequest(Player sender, String payReq, ItemStack item, boolean isPublic) {
        DBAddSellOrder(sender, payReq,item);
        /*
        System.out.println(checksellorder(payReq));
        if(!checksellorder(payReq)) {
            sender.sendMessage("§c your invoice was wrong or to long. try splitting the invoice with /sell 1 'first part' and /sell 2 'second part'");
            return;
        }

        sellOrders.add(new SellOrder(item, payReq, isPublic, sender));
        sender.getInventory().remove(item);
        if(!isPublic){
            QRMapSpawner.SpawnMap(sender, payReq);
        }

        sender.sendMessage(ChatColor.GREEN + " your sell order was added");
        */
    }

    public void AddFirstPartSellRequest(Player sender, String partialPayReq) {
        partialInvoiceMap.put(sender.getDisplayName(), partialPayReq);
        sender.sendMessage(ChatColor.YELLOW + " the first part of your invoice was added");
    }

    public void AddSecondPartSellRequest(Player sender, String partialPayReq, ItemStack item) {
        String payReq = partialInvoiceMap.get(sender.getDisplayName()) + partialPayReq;
        if(!checksellorder(payReq)) {
            sender.sendMessage("§c something went wrong, try splitting the invoice with /sell 1 'first part' and /sell 2 'second part'");
            return;
        }
        DBAddSellOrder(sender, payReq, item);/*
        sellOrders.add(new SellOrder(item, payReq, true, sender));

        sender.getInventory().remove(item);

        sender.sendMessage(ChatColor.GREEN + " your sell order was added");
            */
    }

    boolean checksellorder(String payReq) {
        PayReq decodePayReq;
        try {
            decodePayReq = lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(payReq).build());
        } catch (Exception e) {
            System.out.println("caught exception " + e);
            return false;
        }
        if(decodePayReq.isInitialized())
            return true;
        return false;
    }



    public void listSellOrders(Player sender){
        DbListSellOrders(sender);
        /*
        if(sellOrders.size() >= 0) {
            for (int i = 0; i < sellOrders.size(); i++) {
                SellOrder temp = sellOrders.get(i);
                if (!temp.claimed && temp.isPublic)
                 sender.sendMessage(i + " item: " + temp.item + " cost: " + lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getNumSatoshis());
            }
        }*/
    }

    public void listSellOrders(Player sender, Material filter) {
        Set<String> keys = sellOrderCfg.getKeys(false);
        List<String> keyList = new ArrayList<String>(keys);
        if(keyList.size()> 0 ){
            for (int i = 0; i < keyList.size(); i++) {
                SellOrder temp = (SellOrder) sellOrderCfg.get(keyList.get(i));
                if (!temp.claimed && temp.isPublic && temp.item.getType() == filter)
                    sender.sendMessage(i + " item: " + temp.item + " cost: " + lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getNumSatoshis());
            }
        }
    }
    /*
    public void expireOrders() {
        Set<String> keys = sellOrderCfg.getKeys(false);
        List<String> keyList = new ArrayList<String>(keys);
        if(keyList.size()> 0 ){
            for (int i = 0; i < keyList.size(); i++) {
                SellOrder temp = (SellOrder) sellOrderCfg.get(keyList.get(i));
                lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).
                if (!temp.claimed && temp.isPublic)
                    sender.sendMessage(i + " item: " + temp.item + " cost: " + lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getNumSatoshis());
            }
        }
    }*/

    public void DbListSellOrders(Player sender) {
        Set<String> keys = sellOrderCfg.getKeys(false);
        List<String> keyList = new ArrayList<String>(keys);
        if(keyList.size()> 0 ){
            for (int i = 0; i < keyList.size(); i++) {
                SellOrder temp = (SellOrder) sellOrderCfg.get(keyList.get(i));
                if (!temp.claimed && temp.isPublic) {

                    String text = i + " item: " + temp.item + " cost: " + lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getNumSatoshis();

                    JSONMessage.create(text)
                            .color(ChatColor.GOLD)
                            .runCommand("/getsellorder "+ i).send(sender);
                }

            }
        }
    }


    public void DBgetSellOrder(Player sender, int number) {
        Set<String> keys = sellOrderCfg.getKeys(false);
        List<String> keyList = new ArrayList<String>(keys);
        SellOrder sellOrder = (SellOrder) sellOrderCfg.get(keyList.get(number));
        if (!sellOrder.claimed) {
            sender.sendMessage(sellOrder.payReq);
            QRMapSpawner.SpawnMap(sender, sellOrder.payReq);
        } else {
            sender.sendMessage("item already claimed");
        }
    }
    public void getSellOrder(Player sender, int number) {
        DBgetSellOrder(sender, number);/*
        SellOrder sellOrder = sellOrders.get(number);
        if (!sellOrder.claimed) {
            sender.sendMessage(sellOrder.payReq);
            QRMapSpawner.SpawnMap(sender, sellOrder.payReq);
        } else {
            sender.sendMessage("item already claimed");
        }
        */

    }

    public void claimItem(Player sender, String preimage){
        ItemStack item = DBFindMatch(preimage);
        if (item != null)
            sender.getInventory().addItem(item);
        /*
        ItemStack item = findMatch(preimage);
        if (item != null)
            sender.getInventory().addItem(item);*/
    }

    public void removeSellOrders(Player p) {
        DBgetMySellOrders(p);
        /*
        for (int i = sellOrders.size() - 1; i >= 0; i--) {
            if(sellOrders.get(i).player == p) {
                ItemStack item = sellOrders.get(i).item;
                sellOrders.get(i).claimed = true;
                p.getInventory().addItem(item);
            }
        }*/
    }

    public void DBgetMySellOrders(Player p) {
        Set<String> keys = sellOrderCfg.getKeys(false);
        List<String> keyList = new ArrayList<String>(keys);
        if (keyList.size() > 0) {
            for (int i = 0; i < keyList.size(); i++) {
                SellOrder temp = (SellOrder) sellOrderCfg.get(keyList.get(i));
                if(temp.player == p.getDisplayName()) {
                    ItemStack item = temp.item;
                    temp.claimed = true;
                    sellOrderCfg.set(temp.payReq, temp);
                    try{
                        sellOrderCfg.save(sellOrderFile);
                        p.getInventory().addItem(item);

                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    ItemStack DBFindMatch(String preimage) {
        try {
            String paymentHashIn = Sha.hash256(preimage);
            Set<String> keys = sellOrderCfg.getKeys(false);
            List<String> keyList = new ArrayList<String>(keys);
            if (keyList.size() > 0) {
                for (int i = 0; i < keyList.size(); i++) {
                    SellOrder temp = (SellOrder) sellOrderCfg.get(keyList.get(i));

                    String paymentHashCalc = lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getPaymentHash();

                    if (paymentHashCalc.equals(paymentHashIn) && !temp.claimed) {

                        ItemStack item = temp.item;
                        temp.claimed = true;
                        sellOrderCfg.set(temp.payReq, temp);
                        try{
                            sellOrderCfg.save(sellOrderFile);
                            return item;

                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        }catch (Exception se) {
            System.out.println(se.getMessage());
        }
        return null;
    }
    /*
    ItemStack findMatch(String preimage){
        try {
            String paymentHashIn = Sha.hash256(preimage);
            if(sellOrders.size() >= 0) {
                for (int i = 0; i < sellOrders.size(); i++) {
                    SellOrder temp = sellOrders.get(i);
                    String paymentHashCalc = lndRpc.blockingStub.decodePayReq(PayReqString.newBuilder().setPayReq(temp.payReq).build()).getPaymentHash();

                    if( paymentHashCalc.equals(paymentHashIn) && !sellOrders.get(i).claimed) {

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
    }*/


}
