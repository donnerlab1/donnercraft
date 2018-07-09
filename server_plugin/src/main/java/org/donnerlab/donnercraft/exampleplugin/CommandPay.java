package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPay implements CommandExecutor {
    public ExamplePlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 2) {
                    player.sendMessage(ChatColor.RED + "wrong Command: use /pay playername payreq");
                }

                Player recipient = Bukkit.getPlayer(args[0]).getPlayer();
                recipient.sendMessage(args[1]);
                server.AddPlayerPayRequest(player, recipient, args[1]);
            }
        }
        return true;
    }

    public CommandPay(ExamplePlugin server){this.server=server;}
}
