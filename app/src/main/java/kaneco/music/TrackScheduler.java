package kaneco.music;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean loopQueue = false;
    private boolean paused = false;
    private TextChannel txtChannel;
	private AudioChannel audioChannel;
	private final HashMap<Member, Boolean> playStart;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
		playStart = new HashMap<>();
    }

    public void queue(AudioTrack track){
        if(!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

	public void arrayToQueue(List<AudioTrack> list) {
		purgeQueue();
		for( int i = 0; i < list.size(); i++){
			queue(list.get(i));
		}
	}

	public void removeFromQueue(AudioTrack track){
		this.queue.remove(track);
	}

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

	public boolean containsMemberPlayStart(Member member) {
		return playStart.containsKey(member);
	}

	public void addPlayStart(Member member, boolean playstart) {
		playStart.put(member, playstart);
	}

	public void removePlayStart(Member member) {
		playStart.remove(member);
	}

	public boolean getPlayStart(Member member) {
		return playStart.get(member);
	} 

    public void setTextChannel(TextChannel txtChannel) {
        this.txtChannel = txtChannel;
    }

    public TextChannel geTextChannel(){
        return txtChannel;
    }

	public void setAudioChannel(AudioChannel channel) {
		this.audioChannel = channel;
	}

	public AudioChannel getAudioChannel() {
		return audioChannel;
	}

    public void pauseTrack() {
        this.paused = !paused;
        player.setPaused(paused);
    }

    public void stop(){
        player.stopTrack();
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    public void purgeQueue() {
        this.queue.clear();
    }

    public void setLoopQueue(boolean loop) {
        loopQueue = loop;
    }

    public boolean isLoopQueue(){
        return loopQueue;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(loopQueue) {
            queue(track.makeClone());
        }

        if(endReason.mayStartNext) {
            nextTrack();
        }    
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if(txtChannel != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Tocando Agora");
            builder.addField("Musica", "["+track.getInfo().title+"]("+track.getInfo().uri+")", false);
            builder.setThumbnail("https://img.youtube.com/vi/" + track.getInfo().uri.split("v=")[1] + "/0.jpg");
            builder.setTimestamp(OffsetDateTime.now());
            txtChannel.sendMessageEmbeds(builder.build()).queue();
        }
    }

}

