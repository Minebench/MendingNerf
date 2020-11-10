package de.minebench.mendingnerf.listeners;

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
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class NerfListener implements Listener {

    private final MendingNerf plugin;

    public NerfListener(MendingNerf plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        if (!plugin.isNerfingEnabled()
                || event.getResult() == null
                || event.getViewers().get(0).getGameMode() == GameMode.CREATIVE
                || event.getViewers().get(0).hasPermission("mendingnerf.bypass.anvil")) {
            return;
        }

        for (Map.Entry<Enchantment, Integer> ench : event.getResult().getEnchantments().entrySet()) {
            if (ench.getValue() > ench.getKey().getMaxLevel()) {
                event.setResult(null);

                for (HumanEntity viewer : event.getViewers()) {
                    if (viewer instanceof Player) {
                        ((Player) viewer).updateInventory();
                    }
                }
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMend(PlayerItemMendEvent event) {
        if (!plugin.isNerfingEnabled() || !plugin.isMendingNerfed() || !(event.getItem().getItemMeta() instanceof Repairable)) {
            return;
        }

        ItemMeta meta = event.getItem().getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        int itemRepairCount = dataContainer.getOrDefault(plugin.REPAIR_COUNT_KEY, PersistentDataType.INTEGER, 0);

        int increaseCostEach = (int) (event.getItem().getType().getMaxDurability() * plugin.getIncreaseCostEachModifier());
        if (((Repairable) meta).getRepairCost() >= plugin.getMaxRepairCost() && itemRepairCount > increaseCostEach && !event.getPlayer().hasPermission("mendingnerf.bypass.mending")) {
            event.setRepairAmount(0);
            return;
        }

        double expNeeded = plugin.getBaseExpForRepair() + ((Repairable) meta).getRepairCost() * plugin.getRepairCostModifier();

        double availableExp = dataContainer.getOrDefault(plugin.STORED_EXP_KEY, PersistentDataType.DOUBLE, 0.0) + event.getExperienceOrb().getExperience();

        boolean updated = false;
        plugin.debug(event.getPlayer(), "repairAmount: " + event.getRepairAmount() + " exp: " + event.getExperienceOrb().getExperience() + " repairCost: " + ((Repairable) meta).getRepairCost());
        plugin.debug(event.getPlayer(), "expNeeded: " + expNeeded + " itemRepairCount: " + itemRepairCount + " availableExp: " + availableExp);
        if (availableExp >= expNeeded) {
            int repairTimes = (int) (availableExp / expNeeded);
            int maxRepair = ((Damageable) meta).getDamage();
            repairTimes = repairTimes > maxRepair ? maxRepair : repairTimes;
            event.setRepairAmount(repairTimes);
            boolean increaseRepairCost = itemRepairCount % increaseCostEach + repairTimes >= increaseCostEach;
            itemRepairCount += repairTimes;

            dataContainer.set( plugin.REPAIR_COUNT_KEY, PersistentDataType.INTEGER, itemRepairCount);

            availableExp = availableExp - repairTimes * expNeeded;
            plugin.debug(event.getPlayer(), "repairTimes: " + repairTimes + " increaseCostEach: " + increaseCostEach + " availableExp: " + availableExp);
            if (repairTimes == maxRepair) {
                event.getPlayer().giveExp((int) availableExp);
                availableExp = 0;
            }

            if (increaseRepairCost) {
                ((Repairable) meta).setRepairCost(((Repairable) meta).getRepairCost() + 1);
                plugin.debug(event.getPlayer(), "increaseCost: " + 1 + " itemRepairCount: " + itemRepairCount);
            }
            updated = true;
        } else {
            event.setRepairAmount(0);
        }
        event.getExperienceOrb().setExperience(0);
        if (availableExp == 0) {
            if (dataContainer.has( plugin.STORED_EXP_KEY, PersistentDataType.DOUBLE)) {
                dataContainer.remove( plugin.STORED_EXP_KEY);
                updated = true;
            }
        } else {
            dataContainer.set( plugin.STORED_EXP_KEY, PersistentDataType.DOUBLE, availableExp);
            updated = true;
        }
        if (updated) {
            event.getItem().setItemMeta(meta);
        }
    }
}
