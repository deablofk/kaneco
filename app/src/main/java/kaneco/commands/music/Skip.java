package kaneco.commands.music;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Skip extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		AudioChannel authorChannel = author.getVoiceState().getChannel();
		AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();

		if (authorChannel == null)
			return;
		if (botChannel != null && botChannel != authorChannel) {
			sendMessageEmbeds(channel, KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Skip")
					.setDescription("Você não está no mesmo canal de voz do bot.").build());
			return;
		}

		GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);

		if (msgParams.length > 1) {
			int totalSkip = Integer.parseInt(msgParams[1]);
			List<AudioTrack> songs = new ArrayList<>(manager.scheduler.getQueue());

			if (songs.size() > totalSkip) {
				for (int i = 0; i < totalSkip; i++) {
					manager.scheduler.removeFromQueue(songs.get(i));
				}

				manager.scheduler.nextTrack();
			}
		} else {
			manager.scheduler.nextTrack();
		}

		if (hook() != null)
			hook().deleteOriginal().queue();

	}

}
