package kaneco.commands.music;

import kaneco.api.Command;
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

		if(authorChannel == null)
			return;
		if(botChannel != null && botChannel != authorChannel) {
			sendMessageEmbeds(channel, KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Skip")
					.setDescription("Você não está no mesmo canal de voz do bot.").build());
			return;
		}

		if(hook() != null)
			hook().deleteOriginal().queue();

		PlayerManager.getInstance().getGuildMusicManger(channel.getGuild()).scheduler.nextTrack();
    }

}

