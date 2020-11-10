package de.minebench.mendingnerf.commands;

/*
 * MendingNerf
 * Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.minebench.mendingnerf.MendingNerf;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostCommand implements CommandExecutor {

    private final MendingNerf plugin;

    public RepairCostCommand(MendingNerf plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Can only be executed by a player!");
            return true;
        }
        ItemStack itemStack = ((Player) sender).getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            plugin.sendLang(sender, ChatMessageType.SYSTEM, "no-item-in-hand");
            return true;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof Repairable)) {
            plugin.sendLang(sender, ChatMessageType.SYSTEM, "item-not-repairable");
            return true;
        }

        if (args.length > 0 && sender.hasPermission(command.getPermission() + ".set")) {
            try {
                int oldCost = ((Repairable) meta).getRepairCost();
                ((Repairable) meta).setRepairCost(Integer.parseInt(args[0]));
                itemStack.setItemMeta(meta);
                plugin.sendLang(sender, ChatMessageType.SYSTEM, "set-repaircost",
                        "cost", String.valueOf(((Repairable) meta).getRepairCost()),
                        "oldcost", String.valueOf(oldCost)
                );
            } catch (NumberFormatException e) {
                plugin.sendLang(sender, ChatMessageType.SYSTEM, "invalid-cost",
                        "input", args[0]
                );
            }
        } else {
            plugin.sendLang(sender, ChatMessageType.SYSTEM, "repaircost",
                    "cost", String.valueOf(((Repairable) meta).getRepairCost())
            );
        }
        return true;
    }
}
