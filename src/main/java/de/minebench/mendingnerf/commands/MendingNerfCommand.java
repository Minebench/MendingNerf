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
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MendingNerfCommand implements CommandExecutor {

    private final MendingNerf plugin;

    public MendingNerfCommand(MendingNerf plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if ("enable".equalsIgnoreCase(args[0])) {
                plugin.reloadConfig();
                plugin.getConfig().set("enabled", true);
                plugin.saveConfig();
                plugin.loadConfig();
                plugin.sendLang(sender, ChatMessageType.SYSTEM, "enabled");
                return true;

            } else if("disable".equalsIgnoreCase(args[0])) {
                plugin.reloadConfig();
                plugin.getConfig().set("enabled", false);
                plugin.saveConfig();
                plugin.loadConfig();
                plugin.sendLang(sender, ChatMessageType.SYSTEM, "disabled");
                return true;

            } else if("reload".equalsIgnoreCase(args[0])) {
                boolean enabledBefore = plugin.isNerfingEnabled();
                plugin.loadConfig();
                plugin.sendLang(sender, ChatMessageType.SYSTEM, "reloaded");
                if (enabledBefore != plugin.isNerfingEnabled()) {
                    plugin.sendLang(sender, ChatMessageType.SYSTEM, plugin.isNerfingEnabled() ? "enabled" : "disabled");
                }
                return true;
            } else if ("debug".equalsIgnoreCase(args[0])) {
                Player target;
                if (args.length > 1) {
                    target = plugin.getServer().getPlayer(args[1]);
                    if (target == null) {
                        plugin.sendLang(sender, ChatMessageType.SYSTEM, "no-player-found", "name", args[1]);
                        return true;
                    }
                } else if (sender instanceof Player) {
                    target = (Player) sender;
                } else {
                    sender.sendMessage(ChatColor.RED + "Run /" + label + " debug <playername> from the console!");
                    return true;
                }
                if (plugin.getDebugPlayers().contains(target.getUniqueId())) {
                    plugin.getDebugPlayers().remove(target.getUniqueId());
                } else {
                    plugin.getDebugPlayers().add(target.getUniqueId());
                }
                sender.sendMessage("Debug mode " + (plugin.getDebugPlayers().contains(target.getUniqueId()) ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled") + (target == sender ? "" : " for " + target.getName()));
                return true;
            }
        } else {
            plugin.sendLang(sender, ChatMessageType.SYSTEM, plugin.isNerfingEnabled() ? "enabled" : "disabled");
        }
        return false;
    }
}
