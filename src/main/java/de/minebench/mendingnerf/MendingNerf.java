package de.minebench.mendingnerf;

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

import de.minebench.mendingnerf.commands.MendingNerfCommand;
import de.minebench.mendingnerf.commands.RepairCostCommand;
import de.minebench.mendingnerf.listeners.ChestShopListener;
import de.minebench.mendingnerf.listeners.NerfListener;
import de.themoep.minedown.MineDown;
import de.themoep.utils.lang.bukkit.LanguageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public final class MendingNerf extends JavaPlugin {

    public final NamespacedKey REPAIR_COUNT_KEY = new NamespacedKey(this, "repair-count");
    public final NamespacedKey STORED_EXP_KEY = new NamespacedKey(this, "stored-exp");

    private boolean mendingNerf;
    private boolean nerfEnabled;
    private int maxRepairCost;
    private double increaseCostEachModifier;
    private double repairCostModifier;
    private Set<UUID> debugPlayers = new HashSet<>();

    private LanguageManager lang;
    private double baseExpForRepair;

    @Override
    public void onEnable() {
        // Plugin startup logic
        loadConfig();
        getCommand("mendingnerf").setExecutor(new MendingNerfCommand(this));
        getCommand("repaircost").setExecutor(new RepairCostCommand(this));

        getServer().getPluginManager().registerEvents(new NerfListener(this), this);

        if (getServer().getPluginManager().isPluginEnabled("ChestShop")) {
            getServer().getPluginManager().registerEvents(new ChestShopListener(this), this);
        }
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        lang = new LanguageManager(this, getConfig().getString("default-lang"));
        nerfEnabled = getConfig().getBoolean("enabled");
        mendingNerf = getConfig().getBoolean("mending-nerf");
        maxRepairCost = getConfig().getInt("max-repair-cost");
        baseExpForRepair = getConfig().getDouble("base-exp-for-repair");
        increaseCostEachModifier = getConfig().getInt("increase-cost-step-modifier");
        repairCostModifier = getConfig().getDouble("repair-cost-modifier");
    }

    public BaseComponent[] getLang(CommandSender sender, String key, String... replacements) {
        return MineDown.parse(lang.getConfig(sender).get(key), replacements);
    }

    public BaseComponent[] getLang(CommandSender sender, String key, Map<String, BaseComponent[]> replacements) {
        return new MineDown(lang.getConfig(sender).get(key)).replace(replacements).toComponent();
    }

    public void debug(Player player, String string) {
        if (debugPlayers.contains(player.getUniqueId())) {
            log(Level.INFO, ChatColor.GRAY + "[DEBUG] " + string);
        }
    }

    private void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public boolean isNerfingEnabled() {
        return nerfEnabled;
    }

    public Set<UUID> getDebugPlayers() {
        return debugPlayers;
    }

    public boolean isMendingNerfed() {
        return mendingNerf;
    }

    public double getIncreaseCostEachModifier() {
        return increaseCostEachModifier;
    }

    public int getMaxRepairCost() {
        return maxRepairCost;
    }

    public double getRepairCostModifier() {
        return repairCostModifier;
    }

    public double getBaseExpForRepair() {
        return baseExpForRepair;
    }
}
