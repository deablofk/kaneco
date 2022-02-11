package kaneco.api;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ICommand {

	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams);

	public int params();

	public Permission hasPermission();

}
