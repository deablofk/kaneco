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

public class Ban extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		Member memberBan = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Ban");
		if (memberBan == null) {
			sendMessageEmbeds(channel, eb.setDescription("Usuário não encontrado.").build());
			return;
		}

		String reason = String.join(" ", Arrays.copyOfRange(msgParams, 2, msgParams.length));

		guild.ban(memberBan, 0, "Motivo: " + reason + " | Moderador " + author.getNickname()).queue();
		eb.addField("Usuário:", memberBan.getUser().getName(), false);
		eb.addField("Moderador:", author.getUser().getName(), false);
		eb.addField("Motivo:", reason, false);
		sendMessageEmbeds(channel, eb.build());
		memberBan.getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();

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
