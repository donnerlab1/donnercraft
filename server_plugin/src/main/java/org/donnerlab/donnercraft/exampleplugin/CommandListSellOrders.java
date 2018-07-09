package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class CommandListSellOrders implements CommandExecutor {

    ExamplePlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 0) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /listsellorders");
                    return false;

                }
                server.listSellOrders(player);
            }
        }
        return true;
    }

    public CommandListSellOrders(ExamplePlugin server){
        this.server = server;
    }
}
