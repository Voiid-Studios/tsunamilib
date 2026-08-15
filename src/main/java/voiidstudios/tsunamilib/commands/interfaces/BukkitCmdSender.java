package voiidstudios.tsunamilib.commands.interfaces;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class BukkitCmdSender implements CmdSender {
    private final CommandSender sender;
    private final String prefix;

    public BukkitCmdSender(CommandSender sender, String prefix) {
        this.sender = sender;
        this.prefix = prefix == null ? "" : prefix;
    }

    public void sendMsg(String msg, Object... args) {
        String formatted = args.length > 0 ? String.format(msg, args) : msg;
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', formatted));
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isPlayer() {
        return sender instanceof Player;
    }
}