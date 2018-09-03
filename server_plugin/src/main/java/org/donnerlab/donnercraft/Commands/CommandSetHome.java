package org.donnerlab.donnercraft.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import org.donnerlab.donnercraft.DonnerCraftPlugin;

import java.io.File;

public class CommandSetHome implements CommandExecutor {
    DonnerCraftPlugin plugin;

    public CommandSetHome(DonnerCraftPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        if(args.length == 1) {
            String world = p.getWorld().getName();
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            float yaw = p.getLocation().getYaw();
            float pitch = p.getLocation().getPitch();
            plugin.AddSetHomeRequest(p,args[0],world,x,y,z,pitch,yaw);
        } else {
            p.sendMessage("§c/sethome [name] ");
        }
        return true;
    }
}
