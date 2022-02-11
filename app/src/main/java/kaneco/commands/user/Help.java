package kaneco.commands.user;

import java.util.ArrayList;
import java.util.List;

import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;

public class Help extends Command{

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder embed = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), "📋 Ajuda");
		embed.setDescription("Selecione o módulo para ajuda.\n\nMódulos Disponíveis:\n<:black_arrow_right:940445321652215829> moderation\n<:black_arrow_right:940445321652215829> music\n<:black_arrow_right:940445321652215829> minecraft\n<:black_arrow_right:940445321652215829> users");
		embed.setImage("https://adviyalive.b-cdn.net/wp-content/uploads/2018/11/Help-Desk-Support-banner-e1484400846872.png");

		List<ItemComponent> components = new ArrayList<>();

		SelectMenu menu = SelectMenu.create("helpmenu")
			.addOption("music", "musicdesc")
			.addOption("moderation", "moderationdesc")
			.addOption("minecraft", "minecraftdesc")
			.addOption("users", "usersdesc")
			.build();
		components.add(menu);

		if(hook() == null)
			channel.sendMessageEmbeds(embed.build()).setActionRow(components).queue();
		else
			hook().editOriginalEmbeds(embed.build()).setActionRow(components).queue();
	}

}

