package kaneco.commands.music;

import kaneco.api.Command;
import kaneco.music.PlayerManager;
import kaneco.music.TrackScheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class LoopQueue extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		TrackScheduler scheduler = PlayerManager.getInstance().getGuildMusicManger(guild).scheduler;
		scheduler.setLoopQueue(!scheduler.isLoopQueue());
		EmbedBuilder builder = new EmbedBuilder();

		if (scheduler.isLoopQueue()) {
			builder.setDescription("Repetir queue ativado.");
		} else {
			scheduler.getQueue().clear();
			builder.setDescription("Repetir queue desativado.\n Limpando Fila Atual...");
		}

		sendMessageEmbeds(channel, builder.build());
	}
}
