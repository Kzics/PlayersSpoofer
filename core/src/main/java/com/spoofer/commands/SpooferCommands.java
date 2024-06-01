package com.spoofer.commands;

import com.spoofer.PlayersSpoof;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class SpooferCommands implements CommandExecutor, TabCompleter {

    private final PlayersSpoof plugin;

    public SpooferCommands(final PlayersSpoof plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage("§c/spoofer multiplier <multiplier>");
            commandSender.sendMessage("§c/spoofer interval <interval>");
            return false;
        }

        if (!commandSender.isOp()) return false;

        if (strings[0].equalsIgnoreCase("multiplier")) {
            if (strings.length != 2) {
                commandSender.sendMessage("§c/spoofer multiplier <multiplier>");
                return false;
            }
            double multiplier = Double.parseDouble(strings[1]);
            commandSender.sendMessage("§aMultiplier set to " + multiplier);
            plugin.getFakePlayerManager().setMultiplier(multiplier);
            return true;
        } else if (strings[0].equalsIgnoreCase("interval")) {
            if (strings.length != 2) {
                commandSender.sendMessage("§c/spoofer interval <interval>");
                return false;
            }
            int interval = Integer.parseInt(strings[1]);
            commandSender.sendMessage("§aInterval set to " + interval);
            plugin.getFakePlayerManager().setJoinInterval(interval);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();
        List<String> commands = Arrays.asList("multiplier", "interval");

        if (strings.length == 1) {
            StringUtil.copyPartialMatches(strings[0], commands, completions);
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("multiplier")) {
                completions.addAll(Arrays.asList("1.0", "1.5", "2.0"));
            } else if (strings[0].equalsIgnoreCase("interval")) {
                completions.addAll(Arrays.asList("10", "20", "30"));
            }
        }

        Collections.sort(completions);
        return completions;
    }
}

