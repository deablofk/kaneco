package kaneco.data;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class PagedEmbed {

	public List<MessageEmbed> pages;
	public int currentPage;

	public PagedEmbed() {
		this.pages = new ArrayList<>();
		this.currentPage = 0;
	}

	public void addPage(MessageEmbed embed) {
		pages.add(embed);
	}

	public void getPage(int pageIndex) {
		pages.get(pageIndex);
	}

	public MessageEmbed getCurrentPage(){
		return pages.get(currentPage);
	}

	public MessageEmbed nextPage(){
		if (currentPage < pages.size() - 1) 
			currentPage += 1;
		return getCurrentPage();
	}

	public MessageEmbed prevPage(){
		if (currentPage > 0)
			currentPage -= 1;

		return getCurrentPage();
	}
}

