package kaneco.commands.moderation;

import java.util.Arrays;

import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Ban extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		String userID = msgParams[1].replaceAll("[<@!>]", "");
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Ban");

		String reason = String.join(" ", Arrays.copyOfRange(msgParams, 2, msgParams.length));

		eb.addField("Usuário:", "<@" + userID + ">", false);
		eb.addField("Moderador:", author.getUser().getName(), false);
		eb.addField("Motivo:", reason, false);
		sendMessageEmbeds(channel, eb.build());

		User user = guild.getJDA().getUserById(userID);
		if (user != null) {
			user.openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();
		}

		guild.ban(userID, 0, "Motivo: " + reason + " | Moderador " + author.getNickname()).queue();
		GuildConfig cfg = config(guild);

		if (cfg == null)
			return;

		TextChannel banChannel = guild.getTextChannelById(cfg.getBanChannel());

		if (banChannel == null)
			return;

		banChannel.sendMessageEmbeds(eb.build()).queue();
	}

	@Override
	public int params() {
		return 2;
	}

	@Override
	public Permission hasPermission() {
		return Permission.BAN_MEMBERS;
	}
}
