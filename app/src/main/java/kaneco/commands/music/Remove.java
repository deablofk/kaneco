package kaneco.commands.music;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Remove extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
		AudioChannel authorChannel = author.getVoiceState().getChannel();
		EmbedBuilder embed = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Remove");

		if(authorChannel == null) {
			sendMessageEmbeds(channel, embed.setDescription("É necessário que você esteja em um canal de voz.").build());
			return;
		}
		if(botChannel != null && authorChannel != botChannel){
			sendMessageEmbeds(channel, embed.setDescription("É necessário que você esteja no mesmo canal do bot.").build());
			return;
		}

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

		sendMessageEmbeds(channel, embed.build());
	}

	@Override
	public int params() {
		return 1;
	}

}

