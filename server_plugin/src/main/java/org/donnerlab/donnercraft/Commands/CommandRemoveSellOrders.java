package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandRemoveSellOrders implements CommandExecutor {

    DonnerCraftPlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;


                if(args.length != 0) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /removesellorders");
                }
                server.removeSellOrders(player);

        }
        return true;
    }

    public CommandRemoveSellOrders(DonnerCraftPlugin server){
        this.server = server;
    }
}
