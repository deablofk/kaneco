package kaneco.data;

import java.util.ArrayList;
import java.util.List;

public class GuildConfig {

	private Long guild;
	private List<Long> blackListChannels = new ArrayList<>();
	private String prefix;
	private String welcomeMessage;
	private Long welcomeChannel;
	private Long welcomeRole;
	private Long muteRole;
	private Long banChannel;

	private List<Long> alertRoles = new ArrayList<>();

	public GuildConfig(long guildId, String prefix) {
		this.guild = guildId;
		this.prefix = prefix;
	}

	public long getGuildId() {
		return guild;
	}

	public List<Long> getBlackListChannles() {
		return blackListChannels;
	}

	public boolean addBlackListChannel(long txtChannel) {
		if (!blackListChannels.contains(txtChannel)) {
			blackListChannels.add(txtChannel);
			return true;
		}

		return false;
	}

	public String getGuildPrefix() {
		return prefix;
	}

	public void setStringPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public Long getWelcomeChannel() {
		return welcomeChannel;
	}

	public void setWelcomeChannel(long welcomeChannel) {
		this.welcomeChannel = welcomeChannel;
	}

	public Long getWelcomeRole() {
		return welcomeRole;
	}

	public void setWelcomeRole(long welcomeRole) {
		this.welcomeRole = welcomeRole;
	}

	public Long getMuteRole() {
		return muteRole;
	}

	public void setMuteRole(long muteRole) {
		this.muteRole = muteRole;
	}

	public Long getBanChannel() {
		return banChannel;
	}

	public void setBanChannel(long banChannel) {
		this.banChannel = banChannel;
	}

	public List<Long> getAlertRoles() {
		return alertRoles;
	}

	public void setAlertRoles(List<Long> alertRoles) {
		this.alertRoles = alertRoles;
	}
}
