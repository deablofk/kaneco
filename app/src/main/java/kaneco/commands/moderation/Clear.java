package kaneco.commands.moderation;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kaneco.api.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Clear extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = new EmbedBuilder();
		int deleteCount = 0;
		try {
			deleteCount = Integer.parseInt(msgParams[1]);
		}
		catch(NumberFormatException e){
			embed.setDescription("não foi possível parsear string to int");
		}

		if (deleteCount > 0 && deleteCount < 101) {
			List<Message> messages = channel.getHistory().retrievePast(deleteCount).complete();
			if(!messages.isEmpty()) {
				channel.purgeMessages(messages);
			}
			embed.setDescription("Foram deletas **" + deleteCount + "** mensagens.");
		}
		else {
			embed.setDescription("É necessário que o total de mensagens seja um valor **maior que 0 e menor que 100**.");
		}

		if(hook() == null)
			channel.sendMessageEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		else
			hook().editOriginalEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}

	@Override
	public int params() {
		return 1;
	}

	@Override
	public Permission hasPermission() {
		return Permission.MESSAGE_MANAGE;
	}
}

