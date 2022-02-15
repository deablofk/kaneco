package kaneco.commands.moderation;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class RemoveWarn extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Remove Warn");
		Member member = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		if (member == null) {
			sendMessageEmbeds(channel, embed.setDescription("Usuário não encontrado.").build());
			return;
		}

		boolean delete = Kaneco.restApi.deleteWarn(member.getId(), msgParams[2]);

		if (delete) {
			embed.setDescription("Warn deletada");
		} else {
			embed.setDescription("Warn não foi deletada.");
		}

		sendMessageEmbeds(channel, embed.build());
	}

	@Override
	public Permission hasPermission() {
		return Permission.MODERATE_MEMBERS;
	}

}
