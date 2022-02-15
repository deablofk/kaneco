package kaneco.commands.music;

import java.util.List;

import kaneco.api.Command;
import kaneco.utils.KanecoUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Controls extends Command {

	@Override
	public void runCommand(Member author, TextChannel channel, Guild guild, String[] msgParams) {
		EmbedBuilder eb = KanecoUtils.defaultCmdEmbed(author, guild.getSelfMember(), " Controles");
		eb.setDescription(
				"Controles de Música na ordem: \n\n-10s, Loop, Play/Pause, Skip, +10s\n\nEvite clicar muito rapido nos botões.");

		List<ItemComponent> comps = KanecoUtils.defaultTrackButtons();

		comps.add(0, Button.primary("-10s", "-10s"));
		comps.add(Button.primary("+10s", "+10s"));

		if (hook() == null)
			channel.sendMessageEmbeds(eb.build()).setActionRow(comps).queue();
		else
			hook().editOriginalEmbeds(eb.build()).setActionRow(comps).queue();
	}

}
