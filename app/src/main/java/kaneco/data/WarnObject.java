package kaneco.data;

public class WarnObject {

	private String user;
	private String guild;
	private String moderator;
	private String description;
	private String id;

	public WarnObject(String userid, String guildid, String moderatorid, String description, String id) {
		this.user = userid;
		this.guild = guildid;
		this.moderator = moderatorid;
		this.description = description;
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getGuild() {
		return guild;
	}

	public void setGuild(String guild) {
		this.guild = guild;
	}

	public String getModerator() {
		return moderator;
	}

	public void setModerator(String moderator) {
		this.moderator = moderator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
