package kaneco.commands.music;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Disconnect extends Command {
	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		AudioChannel botChannel = guild.getSelfMember().getVoiceState().getChannel();
		AudioChannel authorChannel = author.getVoiceState().getChannel();
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Disconnect");

		if (authorChannel == null) {
			sendMessageEmbeds(channel, eb.setDescription("É necessário que você esteja em um canal de voz.").build());
			return;
		}
		if (botChannel != null && botChannel != authorChannel) {
			sendMessageEmbeds(channel,
					eb.setDescription("É necessário que você esteja no mesmo canal do bot.").build());
			return;
		}

		GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);
		manager.scheduler.purgeQueue();
		manager.scheduler.stop();
		guild.getAudioManager().closeAudioConnection();
		sendMessageEmbeds(channel, eb.setDescription("Até a próxima.").build());
	}
}
