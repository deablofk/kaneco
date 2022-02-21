package kaneco.commands.moderation;

import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Slowmode
 */
public class Slowmode extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Slowmode");
		int slowAmount = Integer.parseInt(msgParams[1]);
		channel.getManager().setSlowmode(slowAmount).queue();

		eb.setDescription("Modo lento aplicado: " + slowAmount);
		sendMessageEmbeds(channel, eb.build());
	}

	@Override
	public int params() {
		return 1;
	}
}
