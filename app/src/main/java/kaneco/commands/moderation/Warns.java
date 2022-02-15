package kaneco.commands.moderation;

import java.util.List;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.data.WarnObject;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Warns extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		Member member = guild.retrieveMemberById(msgParams[1].replaceAll("[<@!>]", "")).complete();
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Warns");
		if (member == null) {
			sendMessageEmbeds(channel, eb.setDescription("Usuário não encontrado.").build());
			return;
		}

		List<WarnObject> warnList = Kaneco.restApi.getWarns(member.getId());
		String warnMessage = "";
		for (int i = 0; i < warnList.size(); i++) {
			WarnObject warn = warnList.get(i);
			if (warn.getGuild().equals(guild.getId()))
				warnMessage += warn.getId() + " - " + warn.getDescription() + "\n";
		}

		eb.setTitle(member.getUser().getName());
		eb.setDescription(warnMessage);

		sendMessageEmbeds(channel, eb.build());
	}

	@Override
	public Permission hasPermission() {
		return Permission.MODERATE_MEMBERS;
	}
}
