package kaneco.utils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class KanecoUtils {

	public static EmbedBuilder defaultCmdEmbed(Member member, Member author, String cmd) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Comando |" + cmd, null, author.getUser().getEffectiveAvatarUrl());
		eb.setFooter("Requisitado por: " + member.getUser().getName(), member.getUser().getEffectiveAvatarUrl());

		return eb;
	}

	public static EmbedBuilder defaultTrackEmbed(AudioTrack track, Member member) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Música adicionada");
		builder.setDescription("\n[" + track.getInfo().title + "](" + track.getInfo().uri + ")");
		builder.setThumbnail("https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/0.jpg");
		builder.setFooter(member.getEffectiveName(), member.getEffectiveAvatarUrl());
		builder.setTimestamp(OffsetDateTime.now());

		return builder;
	}

	public static List<ItemComponent> defaultTrackButtons() {
		List<ItemComponent> items = new ArrayList<>();

		items.add(Button.primary("loop", Emoji.fromUnicode("U+1F501")));
		items.add(Button.primary("playpause", Emoji.fromUnicode("U+23EF")));
		items.add(Button.primary("next", Emoji.fromUnicode("U+23ED")));

		return items;
	}

	public static TimeUnit getTimeUnitFromStr(String str) {
		TimeUnit unit = TimeUnit.SECONDS;
		switch (str.toLowerCase().substring(str.length() - 1)) {
			case "m":
				unit = TimeUnit.MINUTES;
				break;
			case "h":
				unit = TimeUnit.HOURS;
				break;
			case "d":
				unit = TimeUnit.DAYS;
				break;
			default:
				unit = TimeUnit.SECONDS;
				break;
		}
		return unit;
	}

}
