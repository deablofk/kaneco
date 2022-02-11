package kaneco.commands.music;

import kaneco.api.Command;
import kaneco.music.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Skip extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
        AudioChannel audioChannel = author.getVoiceState().getChannel();
        Member bot = channel.getGuild().getMemberById(channel.getJDA().getSelfUser().getIdLong());

        if(audioChannel != null && audioChannel == bot.getVoiceState().getChannel()) {
			if(hook() != null)
				hook().deleteOriginal().queue();

            PlayerManager.getInstance().getGuildMusicManger(channel.getGuild()).scheduler.nextTrack();
        }
        else {
			if(hook() == null)
				channel.sendMessage("Você não esta no canal de voz").queue();
			else
				hook().editOriginal("Você não esta no canal de voz").queue();
        }
    }

}

