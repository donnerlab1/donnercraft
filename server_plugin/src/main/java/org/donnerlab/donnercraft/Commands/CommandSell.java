package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandSell implements CommandExecutor {

    DonnerCraftPlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length == 1) {

                    PlayerInventory inv = player.getInventory();
                    //String payReq = server.lndRpc.getPaymentRequest(args[1], Integer.parseInt(args[0]));
                    //player.sendMessage(payReq);
                    if (args.length == 1) {
                        server.AddSellOrderRequest(player, args[0], inv.getItemInMainHand(), true);
                    }
                } else if(args.length == 2) {
                    int part = Integer.parseInt(args[0]);
                    if(part == 1) {
                        server.AddFirstPartSellRequest(player, args[1]);
                    } else if (part == 2) {
                        PlayerInventory inv = player.getInventory();
                        server.AddSecondPartSellRequest(player,args[1], inv.getItemInMainHand());
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /sell payreq or /sell 1/2 payreq");
                }
            }
        }
        return true;
    }

    public CommandSell(DonnerCraftPlugin server){
        this.server = server;
    }
}
