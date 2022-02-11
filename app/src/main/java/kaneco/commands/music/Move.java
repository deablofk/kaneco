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

public class Move extends Command {
	
	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();

		if( author.getVoiceState().getChannel() != null) {
			GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);
			List<AudioTrack> songs = new ArrayList<>(manager.scheduler.getQueue());
			
			int oldPos = Integer.parseInt(msgParams[1]);
			int newPos = Integer.parseInt(msgParams[2]); 

			if(songs.size() > oldPos && newPos < songs.size()) {
				AudioTrack track = songs.get(oldPos);
				songs.remove(oldPos);
				songs.add(newPos, track);
				manager.scheduler.arrayToQueue(songs);
				embed.setDescription("A musica [" + track.getInfo().title + "](" + track.getInfo().uri + ") foi movida da posição " + oldPos + " para a posição " + newPos);
			}
			else {
				embed.setDescription("Posição invalida.");
			}
		}
		else{
			embed.setDescription("Não é possível remover uma música se você não estiver em um canal.");
		}

		if(hook() == null)
			channel.sendMessageEmbeds(embed.build()).queue();
		else 
			hook().editOriginalEmbeds(embed.build()).queue();
	}


	@Override
	public int params() {
		return 2;
	}
}
