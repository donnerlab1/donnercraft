package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandListSellOrders implements CommandExecutor {

    DonnerCraftPlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length == 1) {
                    Material filter = Material.matchMaterial(args[0]);
                    if(filter==null) {
                        player.sendMessage(ChatColor.RED + "material not found");
                    } else {
                        server.listSellOrders(player, filter);
                    }

                } else if(args.length == 0) {
                    server.listSellOrders(player);

                } else {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /listsellorders");
                }

            }
        }
        return true;
    }

    public CommandListSellOrders(DonnerCraftPlugin server){
        this.server = server;
    }
}
