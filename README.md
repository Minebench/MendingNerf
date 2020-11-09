# MendingNerf

Minecraft (Bukkit) plugin to nerf Mending by making it limited by repair cost. The repair cost also increases (based on a config) when an item is mended with a limit to the repair cost after which any form of repairing will be diesabled.

Also limits repairing/adding through anvils and features extra info in ChestShop's `/iteminfo` command.

## Config

```yaml
# Whether the whole plugin should be enabled
enabled: true

# The default language to use when no language file exists for the client's language
default-lang: en

# The maximum that the repair cost can grow to. After that the item can't be repaired
max-repair-cost: 100

# Whether mending should be nerfed. If disabled then only anvil repairs will be blocked above the max cost.
mending-nerf: true

# How often the repair cost should be increased (each x mends)
increase-cost-step-modifier: 1.0

# By how much the cost should be increased each time
repair-cost-modifier: 0.2
```

## Commands

 Command                           | Description 
-----------------------------------|-----------------------------------------------------------------
`/mendingnerf enable`              | Enable the whole plugin functions (sets `enabled` config key)
`/mendingnerf disable`             | Disable the whole plugin functions (sets `enabled` config key)
`/mendingnerf reload`              | Reload the config
`/mendingnerf debug [<player>]`    | Enable debugging mode for you or a player to print some mending info in console/log
`/repaircost`                      | Show an item's repair cost
`/repaircost <cost>`               | Set an item's repair cost

## Permissions

 Permission                            | Default | Description 
---------------------------------------|---------|---------------------------------------------------
`mendingnerf.command.mendingnerf`      | op      | Use the `/mendingnerf` admin command
`mendingnerf.command.repaircost`       | true    | Use the `/repaircost` command
`mendingnerf.command.repaircost.set`   | op      | Use the `/repaircost set <cost>` command
`mendingnerf.bypass.mending`           | false   | Allow bypassing of the repair cost block when mending
`mendingnerf.bypass.anvil`             | false   | Allow bypassing of the repair cost block in anvils

## Downlaods

Latest development builds can be obtained from the [Minebench.de Jenkins server](https://ci.minebench.de/job/MendingNerf/).

## License

Licensed under GPLv3.

```
MendingNerf
Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```