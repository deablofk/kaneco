package kaneco.commands.moderation;

import java.util.Arrays;
import java.util.List;

import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * MassBan
 */
public class MassBan extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		List<Long> ids = KanecoUtils.validateIds(msgParams);
		String reason = "" + String.join(" ", Arrays.copyOfRange(msgParams, ids.size() + 1, msgParams.length));

		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, channel.getGuild().getSelfMember(), " Ban");
		eb.addField("Servidor:", guild.getName(), false);
		eb.addField("Moderador:", author.getUser().getName(), false);
		eb.addField("Motivo:", reason, false);

		EmbedBuilder svEb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " MassBan");
		svEb.setDescription("Banidos: ");
		svEb.addField("Moderador", author.getUser().getName(), false);
		svEb.addField("Motivo:", reason, false);

		for (int i = 0; i < ids.size(); i++) {
			Member member = guild.retrieveMemberById(ids.get(i)).complete();
			if (member != null) {
				svEb.appendDescription(" " + member.getAsMention());
				eb.setDescription("**Usuário:**\n<@" + ids.get(i) + ">");
				member.getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();
				guild.ban(ids.get(i) + "", 0, "Motivo: " + reason + " | Moderador " + author.getNickname()).queue();
			}
		}

		sendMessageEmbeds(channel, svEb.build());

		GuildConfig cfg = config(guild);

		if (cfg == null)
			return;

		TextChannel banChannel = guild.getTextChannelById(cfg.getBanChannel());

		if (banChannel == null)
			return;

		banChannel.sendMessageEmbeds(svEb.build()).queue();
	}

	@Override
	public Permission hasPermission() {
		return Permission.BAN_MEMBERS;
	}

	@Override
	public int params() {
		return 2;
	}
}
