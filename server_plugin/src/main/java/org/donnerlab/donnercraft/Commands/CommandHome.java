package org.donnerlab.donnercraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandHome implements CommandExecutor {
    DonnerCraftPlugin plugin;

    public CommandHome(DonnerCraftPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(args.length == 1) {

            plugin.AddHomeRequest(p,args[0]);
        } else {
            p.sendMessage("§c/sethome [name] ");
        }
        return true;
    }
}
