package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandTeleport implements CommandExecutor, CallbackCommand {
    DonnerCraftPlugin server;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 1) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /teleport steps");
                }
                server.AddTeleportRequest(Integer.parseInt(args[0]), player);


            }
        }
        return true;
    }

    public CommandTeleport(DonnerCraftPlugin server) {
        this.server = server;
    }

    @Override
    public void Execute() {

    }
}
