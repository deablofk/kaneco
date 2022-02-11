package kaneco.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class NP extends Command {
	
	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);

		AudioTrack track = manager.player.getPlayingTrack();
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("Musíca Atual");

		if( track != null) {
			long pos = track.getPosition() / 1000;
			long dur = track.getDuration() / 1000;

			int stage = (int)((pos * 30) / dur);

			StringBuilder builder = new StringBuilder();
			builder.append("──────────────────────────────");
			builder.insert(stage, "▶");

			String progressBar = pos/60 +":"+ pos%60 + builder.toString() + dur/60 +":"+ dur%60;

			embed.setDescription("Tocando atualmente: ["+ track.getInfo().title + "](" + track.getInfo().uri + ")\n" + 
					progressBar
					);	
		}
		else {
			embed.setDescription("O bot não está tocando nenhuma música atualmente.");
		}

		if(hook() == null) {
			channel.sendMessageEmbeds(embed.build()).queue();
		}
		else {
			hook().editOriginalEmbeds(embed.build()).queue();
		}
	}
}
