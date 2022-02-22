package kaneco.commands.levelling;

import java.util.concurrent.ExecutionException;

import kaneco.Kaneco;
import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Rank extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Rank");

		long id = author.getIdLong();

		if (msgParams.length > 1) {
			long mentionID = KanecoUtils.validateId(msgParams[1]);
			if (mentionID != 0) {
				id = mentionID;
			}
		}

		try {
			eb.setDescription("RANK: " + Kaneco.userCache.get(id).getXp());
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		sendMessageEmbeds(channel, eb.build());
	}

}
