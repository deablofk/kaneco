package kaneco.api;

import java.util.List;
import java.util.concurrent.ExecutionException;

import kaneco.Kaneco;
import kaneco.data.GuildConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class Command implements ICommand {

	private InteractionHook hook = null;

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
	}

	@Override
	public int params() {
		return 0;	
	}

	@Override
	public Permission hasPermission() {
		return Permission.MESSAGE_SEND;
	}

	public void setHook(InteractionHook hook) {
		this.hook = hook;	
	}

	public InteractionHook hook(){
		return hook;
	}

	public GuildConfig config(Guild guild){
		GuildConfig config = null;

		try {
			config = Kaneco.configCache.get(guild.getId());
		} catch(ExecutionException e ){
			System.out.println("Not possible to get GuildConfig");
			config = new GuildConfig(guild.getId(), guild.getOwnerId(), "./");
			Kaneco.configCache.put(guild.getId(), config);
		}

		return config;
	}

	public void sendMessageEmbeds(TextChannel channel, MessageEmbed msg){
		if(hook() == null)
			channel.sendMessageEmbeds(msg).queue();
		else 
			hook().editOriginalEmbeds(msg).queue();
	}

}
