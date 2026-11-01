package me.xdanez.lookwhatihave;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.xdanez.lookwhatihave.commands.LWIH;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.RegExp;

import java.util.regex.Pattern;

public final class LookWhatIHave extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        LWIH.validateConfig(null);

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("lwih").setExecutor(new LWIH());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        if (requiresPermission() && !player.hasPermission("lookwhatihave.display.item")) return;

        @RegExp String key = "";
        if (ignoreCase()) key += "(?i)";
        key += key();

        TextComponent msg = (TextComponent) e.message();
        if (!Pattern.compile(key).matcher(msg.content()).find()) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) return;

        int amt = item.getAmount();

        e.message(msg.replaceText(TextReplacementConfig.builder().match(key).replacement(
                Component.text(amt > 1 ? amt + "x" : "").append(item.displayName())
        ).build()));
    }

    public static LookWhatIHave plugin() {
        return getPlugin(LookWhatIHave.class);
    }

    public String key() {
        return getConfig().getString("key");
    }

    public boolean ignoreCase() {
        return getConfig().getBoolean("ignore-case-sensitivity");
    }

    public boolean requiresPermission() {
        return getConfig().getBoolean("requires-permission");
    }
}
