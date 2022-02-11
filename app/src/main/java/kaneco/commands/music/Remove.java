package kaneco.commands.music;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Remove extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		if( author.getVoiceState().getChannel() != null) {
			GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);
			List<AudioTrack> songs = new ArrayList<>(manager.scheduler.getQueue());
			
			int songPos = Integer.parseInt(msgParams[1]);
				
			if(songs.size() >= songPos) {
				AudioTrack track = songs.get(songPos);
				manager.scheduler.removeFromQueue(track);
				embed.setDescription("Musica " + track.getInfo().title + " na posição " + songPos + " foi removida da lista.");
			}
			else {
				embed.setDescription("não foi encontrada uma música para a posição " + songPos);
			}
		}
		else {
			embed.setDescription("Não é possível remover uma música se você não estiver em um canal.");
		}

		if(hook() == null){
			channel.sendMessageEmbeds(embed.build()).queue();
		}
		else {
			hook().editOriginalEmbeds(embed.build()).queue();
		}
	}

	@Override
	public int params() {
		return 1;
	}

}

