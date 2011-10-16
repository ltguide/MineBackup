package alexoft.Minebackup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MineBackupCommandListener implements CommandExecutor {
	private final MineBackup plugin;
	
	public MineBackupCommandListener(MineBackup plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("minebackup.manual")) return false;
		
		plugin.executeBackup(sender.getName());
		return true;
	}
}
