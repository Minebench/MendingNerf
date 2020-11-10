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

import com.Acrobot.ChestShop.Events.ItemInfoEvent;
import de.minebench.mendingnerf.MendingNerf;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataType;

public class ChestShopListener implements Listener {

    private final MendingNerf plugin;

    public ChestShopListener(MendingNerf plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChestshopItemInfo(ItemInfoEvent event) {
        if (!plugin.isNerfingEnabled() || !plugin.isMendingNerfed()  || !(event.getItem().getItemMeta() instanceof Repairable)) {
            return;
        }

        boolean canBeRepaired = true;
        if (event.getItem().getItemMeta().getPersistentDataContainer().has(plugin.REPAIR_COUNT_KEY, PersistentDataType.INTEGER)) {
            int itemRepairCount = event.getItem().getItemMeta().getPersistentDataContainer().getOrDefault(plugin.REPAIR_COUNT_KEY, PersistentDataType.INTEGER, 0);

            event.getSender().spigot().sendMessage(plugin.getLang(event.getSender(), "chestshop.repair-count",
                    "count", String.valueOf(itemRepairCount)
            ));

            int increaseCostEach = (int) (event.getItem().getType().getMaxDurability() * plugin.getIncreaseCostEachModifier());
            if (((Repairable) event.getItem().getItemMeta()).getRepairCost() >= plugin.getMaxRepairCost() && itemRepairCount > increaseCostEach) {
                event.getSender().spigot().sendMessage(plugin.getLang(event.getSender(), "chestshop.not-repairable"));
                canBeRepaired = false;
            }
        }
        if (canBeRepaired && event.getItem().getItemMeta().hasEnchant(Enchantment.MENDING)) {
            event.getSender().spigot().sendMessage(plugin.getLang(event.getSender(), "chestshop.exp-required",
                    "exp", String.valueOf((plugin.getBaseExpForRepair() + ((Repairable) event.getItem().getItemMeta()).getRepairCost() * plugin.getRepairCostModifier()))
            ));
        }
    }
}
