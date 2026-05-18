package io.github.crysscoder.dailychest;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class DailyChestPlugin extends JavaPlugin implements CommandExecutor, TabCompleter {
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("daily")).setExecutor(this);
        Objects.requireNonNull(getCommand("daily")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dailychest.reload")) {
                send(sender, "no-permission");
                return true;
            }
            reloadConfig();
            send(sender, "reloaded");
            return true;
        }

        if (!(sender instanceof Player player)) {
            send(sender, "only-player");
            return true;
        }

        if (!player.hasPermission("dailychest.use")) {
            send(player, "no-permission");
            return true;
        }

        claim(player);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("dailychest.reload")) {
            return List.of("reload");
        }
        return List.of();
    }

    private void claim(Player player) {
        String path = "players." + player.getUniqueId();
        long now = System.currentTimeMillis();
        long last = getConfig().getLong(path + ".last", 0L);
        long cooldown = Math.max(1, getConfig().getInt("cooldown-hours", 24)) * 60L * 60L * 1000L;

        if (last + cooldown > now) {
            send(player, "wait", Map.of("time", formatTime(last + cooldown - now)));
            return;
        }

        int streak = getConfig().getInt(path + ".streak", 0) + 1;
        ItemStack reward = reward();
        player.getInventory().addItem(reward).values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        getConfig().set(path + ".last", now);
        getConfig().set(path + ".streak", streak);
        saveConfig();
        send(player, "claimed", Map.of("streak", String.valueOf(streak)));
    }

    private ItemStack reward() {
        List<String> rewards = getConfig().getStringList("rewards");
        String value = rewards.get(ThreadLocalRandom.current().nextInt(rewards.size()));
        String[] parts = value.split(":");
        Material material = Material.matchMaterial(parts[0].toUpperCase(Locale.ROOT));
        int amount = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
        return new ItemStack(material == null ? Material.EMERALD : material, Math.max(1, amount));
    }

    private String formatTime(long millis) {
        long minutes = Math.max(1L, (long) Math.ceil(millis / 60000.0D));
        long hours = minutes / 60L;
        long leftMinutes = minutes % 60L;
        if (hours <= 0) {
            return leftMinutes + " мин";
        }
        return hours + " ч " + leftMinutes + " мин";
    }

    private void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    private void send(CommandSender sender, String key, Map<String, String> values) {
        String prefix = getConfig().getString("messages.prefix", "&7[&aDaily&7]");
        String result = getConfig().getString("messages." + key, "").replace("%prefix%", prefix);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        sender.sendMessage(legacy.deserialize(result));
    }
}
