package me.xdanez.lookwhatihave.commands;

import me.xdanez.lookwhatihave.LookWhatIHave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.constructor.ConstructorException;

import java.util.List;

public class LWIH implements CommandExecutor, TabCompleter {
    public final String RELOAD_PERMISSION = "lookwhatihave.reload";

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload") && commandSender.hasPermission(RELOAD_PERMISSION)) {
            try {
                LookWhatIHave.plugin().reloadConfig();
            } catch (ConstructorException e) {
                commandSender.sendMessage(Component.text("Unable to reload config!").color(TextColor.color(0xff0000)));
                LookWhatIHave.plugin().getLogger().info(e.toString());
                return false;
            }
            validateConfig(commandSender);
            return false;
        }
        showInfoCommand(commandSender);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (commandSender.hasPermission(RELOAD_PERMISSION) && !args[0].equalsIgnoreCase("reload"))
            return List.of("reload");
        return List.of();
    }

    private Component defaultText(String text) {
        return Component.text(text).color(TextColor.color(114, 215, 0));
    }

    private Component specialText(String text) {
        return Component.text(text).color(TextColor.color(44, 152, 255));
    }

    private void showInfoCommand(CommandSender sender) {
        PluginDescriptionFile pdf = LookWhatIHave.plugin().getDescription();
        Component separator = Component.text("--------------------")
                .color(TextColor.color(81, 81, 81));
        String source = pdf.getWebsite();

        assert source != null;
        sender.sendMessage(Component.text(sender instanceof Player ? "" : "\n")
                .append(separator)
                .append(defaultText("\n" + pdf.getName()))
                .append(defaultText(" by "))
                .append(specialText(String.join("", pdf.getAuthors()) + "\n"))
                .append(defaultText(pdf.getDescription() + "\n"))
                .append(defaultText("Version: "))
                .append(specialText(pdf.getVersion() + "\n"))
                .append(defaultText("Source: "))
                .append(specialText(source + "\n").clickEvent(ClickEvent.openUrl(source)))
                .append(separator));
    }

    public static void validateConfig(@Nullable CommandSender sender) {
        if (!validateTag("ignore-case-sensitivity", LookWhatIHave.plugin().getConfig().get("ignore-case-sensitivity")) ||
                !validateTag("requires-permission", LookWhatIHave.plugin().getConfig().get("requires-permission"))) {
            if (sender != null)
                sender.sendMessage(Component.text("Reloaded with errors").color(TextColor.color(0xffff00)));
            return;
        }
        if (sender != null)
            sender.sendMessage(Component.text("Successfully reloaded!").color(TextColor.color(0x00ff00)));
    }

    private static boolean validateTag(String config, Object tag) {
        if (tag != null) {
            String tagStr = tag.toString();
            if (tagStr.equalsIgnoreCase("true") || tagStr.equalsIgnoreCase("false")) return true;
        }
        LookWhatIHave.plugin().getLogger().warning(config + " wrongfully declared");
        return false;
    }
}
