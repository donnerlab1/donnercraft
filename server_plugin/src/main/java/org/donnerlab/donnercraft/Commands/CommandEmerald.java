package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandEmerald implements CommandExecutor {

    DonnerCraftPlugin server;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 1) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /emerald [count]");

                } else {
                //String payReq = server.lndRpc.getPaymentRequest(args[1], Integer.parseInt(args[0]));
                //player.sendMessage(payReq);
                int amt = Integer.parseInt(args[0]);
                if(amt < 1 || amt > 64) {
                    player.sendMessage(ChatColor.RED + "min count 1, max count 64)");
                } else {
                    server.AddEmeraldRequest(player, amt);
                }
                }


            }
        }
        return true;
    }

    public CommandEmerald(DonnerCraftPlugin server) {
        this.server = server;
    }
}