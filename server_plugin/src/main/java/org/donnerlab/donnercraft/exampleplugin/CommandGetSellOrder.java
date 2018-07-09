package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandGetSellOrder implements CommandExecutor {

    ExamplePlugin server;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 1) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /getsellorder index");
                }
                               //String payReq = server.lndRpc.getPaymentRequest(args[1], Integer.parseInt(args[0]));
                //player.sendMessage(payReq);
                server.buyItem(player, Integer.parseInt(args[0]));
            }
        }
        return true;
    }

    public CommandGetSellOrder(ExamplePlugin server) {
        this.server = server;
    }
}
