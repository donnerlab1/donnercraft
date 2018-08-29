package org.donnerlab.donnercraft.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.donnerlab.donnercraft.DonnerCraftPlugin;

public class CommandClaim implements CommandExecutor {

    DonnerCraftPlugin server;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(args != null) {
                if(args.length != 1) {
                    player.sendMessage(ChatColor.RED + "wrond Command: use /claim preimage");
                }
                PlayerInventory inv = player.getInventory();
                //String payReq = server.lndRpc.getPaymentRequest(args[1], Integer.parseInt(args[0]));
                //player.sendMessage(payReq);
                server.claimItem(player, args[0]);
            }
        }
        return true;
    }

    public CommandClaim(DonnerCraftPlugin server) {
        this.server = server;
    }
}
