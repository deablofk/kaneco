package kaneco.commands.music;

import kaneco.api.Command;
import kaneco.music.GuildMusicManager;
import kaneco.music.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Disconnect extends Command{
	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		AudioChannel audioChannel = author.getVoiceState().getChannel();
        Member bot = channel.getGuild().getSelfMember();

        if(audioChannel != null && audioChannel == bot.getVoiceState().getChannel()) {
            GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManger(guild);
            manager.scheduler.purgeQueue();
            manager.scheduler.stop();
            guild.getAudioManager().closeAudioConnection();

			if(hook() == null) 
				channel.sendMessage("Até.").queue();
			else
				hook().editOriginal("Até.").queue();
        }
        else {
			if(hook() == null) 
				channel.sendMessage("É necessário que você esteja no mesmo canal de voz do bot.").queue();
			else
				hook().editOriginal("É necessário que você esteja no mesmo canal de voz do bot.").queue();
        }
    }
}
