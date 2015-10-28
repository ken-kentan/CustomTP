package jp.kentan.minecraft.tp;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomTP extends JavaPlugin {
	private int tp_number_ctp = -1, minute = -1;

	@Override
	public void onEnable() {
		getLogger().info("CustomTPを有効化しました");
	}

	@Override
	public void onDisable() {
		getLogger().info("CustomTPを無効化しました");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = (Player) sender;// get player of do command

		switch (cmd.getName()) {
		case "stp":
			int tp_number;
			try {
				tp_number = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfex) {
				sender.sendMessage(ChatColor.RED + "TPナンバーを正しく入力してください");
				doError(sender, nfex);
				return false;
			}

			if (tp_number < 0 || tp_number > 9) {
				sender.sendMessage(ChatColor.RED + "TPナンバーは0〜9で入力してください");
				sender.sendMessage(ChatColor.RED + "コマンドを正常に実行できませんでした");
				return false;
			}

			Location tplocset = player.getLocation();
			getConfig().set("at.X" + tp_number, tplocset.getX());
			getConfig().set("at.Y" + tp_number, tplocset.getY());
			getConfig().set("at.Z" + tp_number, tplocset.getZ());
			saveConfig();

			sender.sendMessage(ChatColor.AQUA + "TP" + args[0] + "を設定しました");
			break;
		case "ctp":
			if ((sender instanceof Player)) {
				Calendar now = Calendar.getInstance();

				// Init
				if (tp_number_ctp == -1) {
					tp_number_ctp = 0;
					minute = now.get(now.MINUTE);
				}

				if (now.get(now.MINUTE) - minute > 5 || now.get(now.MINUTE) - minute < 0) {
					minute = now.get(now.MINUTE);
					tp_number_ctp++;
				}

				if (tp_number_ctp < 0)
					tp_number_ctp = 9;
				else if (tp_number_ctp > 9)
					tp_number_ctp = 0;

				Location tplocset1 = player.getLocation();
				tplocset1.setX(getConfig().getDouble("at.X" + tp_number_ctp));
				tplocset1.setY(getConfig().getDouble("at.Y" + tp_number_ctp));
				tplocset1.setZ(getConfig().getDouble("at.Z" + tp_number_ctp));

				if (tplocset1 == null) {
					sender.sendMessage(ChatColor.RED + "TP" + tp_number_ctp + "が設定されていません");
					return false;
				}

				if (args.length != 0) {
					Player other = getPlayer(args[0]);
					if (other == null) {
						sender.sendMessage(ChatColor.RED + args[0] + "さんはオフラインです");
						return false;
					}
					other.teleport(tplocset1);
				} else {
					player.teleport(tplocset1);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "ゲーム内から実行してください");
				return false;
			}
			break;
		}
		return true;
	}

	public void doError(CommandSender _sender, Exception _e) {
		_sender.sendMessage(ChatColor.RED + "コマンドを正常に実行できませんでした");
		getLogger().info(_e.toString());
	}

	private Player getPlayer(String name) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

}
