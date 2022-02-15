package kaneco.commands.moderation;

import kaneco.api.Command;
import kaneco.data.GuildConfig;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Unmute extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Unmute");
		Member member = guild.retrieveMemberById(msgParams[1].replace("[<@!>]", "")).complete();
		if (member == null) {
			sendMessageEmbeds(channel, embed.setDescription("Membro não encontrado.").build());
			return;
		}

		if (!member.isTimedOut()) {
			sendMessageEmbeds(channel,
					embed.setDescription("Usuário " + member.getAsMention() + " não estava mutado.").build());
			return;
		}

		member.removeTimeout().queue();
		embed.setDescription("Usuário " + member.getAsMention() + " foi desmutado.");

		GuildConfig cfg = config(guild);
		if (cfg.getMuteRole() != null && guild.getRoleById(cfg.getMuteRole()) != null) {
			guild.removeRoleFromMember(member, guild.getRoleById(cfg.getMuteRole())).queue();
		}

		sendMessageEmbeds(channel, embed.build());
	}

	@Override
	public Permission hasPermission() {
		return Permission.MODERATE_MEMBERS;
	}

	@Override
	public int params() {
		return 1;
	}

}
