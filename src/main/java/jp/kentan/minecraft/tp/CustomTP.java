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
	private int tp_number_ctp = -1,
                minute = -1;

	@Override
	public void onEnable() {
		getLogger().info("CustomTPを有効化しました");
	}

	@Override
	public void onDisable() {
		getLogger().info("CustomTPを無効化しました");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = null;

		switch (cmd.getName()) {
		case "stp":
			if (args[0].equals("time")) {
				int r_time = 0;

				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "時間（分）を入力してください");
					return false;
				}
				
				try {
					r_time = Integer.parseInt(args[1]);
				} catch (NumberFormatException nfex) {
					sender.sendMessage(ChatColor.RED + "時間を正しく入力してください");
					doError(sender, nfex);
					return false;
				}

				if (r_time < 0 || r_time > 60) {
					sender.sendMessage(ChatColor.RED + "時間（分）は0~60の間で設定してください");
					return false;
				}

				getConfig().set("reload_time", r_time);
				saveConfig();

				sender.sendMessage(ChatColor.AQUA + "更新時間を" + args[1] + "分に設定しました");

			} else {
				int tp_number;

				player = (Player) sender;// get player of do command

				try {
					tp_number = Integer.parseInt(args[0]);
				} catch (NumberFormatException nfex) {
					sender.sendMessage(ChatColor.RED + "TPナンバーを正しく入力してください");
					doError(sender, nfex);
					return false;
				}

				if (tp_number < 0 || tp_number > 9) {
					sender.sendMessage(ChatColor.RED + "TPナンバーは0〜9で入力してください");
					return true;
				}

				Location tplocset = player.getLocation();
				getConfig().set("at.X" + tp_number, tplocset.getX());
				getConfig().set("at.Y" + tp_number, tplocset.getY());
				getConfig().set("at.Z" + tp_number, tplocset.getZ());
				saveConfig();

				sender.sendMessage(ChatColor.AQUA + "TP" + args[0] + "を設定しました");
			}
			break;
		case "ctp":

			if ((sender instanceof Player) == false && args.length == 0) {
				sender.sendMessage(ChatColor.RED + "ゲーム内から実行するか、TP対象のプレイヤーを指定してください");
				return false;
			}

			Calendar now = Calendar.getInstance();

			if (args.length != 0) {
				try {
					player = getPlayer(args[0]);
				} catch (Exception e) {
					doError(sender, e);
					return false;
				}

				if (player == null) {
					sender.sendMessage(ChatColor.RED + args[0] + "さんはオフラインです");
					return true;
				}
			} else if ((sender instanceof Player)) {
				player = (Player) sender;// get player of do command
			}

			// Init
			if (tp_number_ctp == -1) {
				tp_number_ctp = 0;
				minute = now.get(now.MINUTE);
			}

			if (now.get(now.MINUTE) - minute > getConfig().getInt("reload_time") - 1
					|| now.get(now.MINUTE) - minute < 0) {
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
				return true;
			}

			player.teleport(tplocset1);
			
			getLogger().info(player.toString() + "をTP" + tp_number_ctp + "にTPしました");

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
