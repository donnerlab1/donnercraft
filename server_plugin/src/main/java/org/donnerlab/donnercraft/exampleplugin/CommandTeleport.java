package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeleport implements CommandExecutor {
    public ExamplePlugin server;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 1) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /teleport steps");
                }
                String payReq = server.AddTeleportRequest(Integer.parseInt(args[0]), player);
                QRMapSpawner.SpawnMap(player, payReq);
                player.sendMessage(payReq);

            }
        }
        return true;
    }

    public CommandTeleport(ExamplePlugin server) {
        this.server = server;
    }
}
