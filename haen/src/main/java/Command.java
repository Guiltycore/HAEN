import com.sethsutopia.utopiai.osu.BeatMap;
import com.sethsutopia.utopiai.osu.OSUPlayer;
import com.sethsutopia.utopiai.osu.RecentPlay;
import com.sethsutopia.utopiai.osu.events.GainedPPEvent;
import com.sethsutopia.utopiai.restful.RestfulException;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import com.sethsutopia.utopiai.osu.events.OSUListener;
import com.sethsutopia.utopiai.restful.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Yves on 13/06/2017.
 */
public class Command extends ListenerAdapter /*implements OSUListener*/ {
    private HashMap<String, Consumer<MessageReceivedEvent>> Commands;
    private HashMap<String, String> description;
    static String commandCall;
    static HashMap<String, Thread> runningThreads = new HashMap<>();
    static HashMap<Integer, ArrayList<MessageChannel>> players;
    public int scanLimit = 5;

    public Command() {
        this.Commands = new HashMap<String, Consumer<MessageReceivedEvent>>();
        this.description = new HashMap<String, String>();
        this.players = new HashMap<Integer, ArrayList<MessageChannel>>();
        runningThreads = new HashMap<String, Thread>();
        commandCall = "$";
    }

    public void addCommand(Consumer<MessageReceivedEvent> command, String name, String de) {
        Commands.put(name, command);
        description.put(name, de);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContent();
        if (msg.startsWith(commandCall) && msg.length() > 2) {
            String cmd = msg.substring(1, msg.contains(" ") ? msg.indexOf(" ") : msg.length());
            if (cmd.contentEquals("help")) {
                final StringBuilder send = new StringBuilder("Help command list: \n");
                description.forEach((name, description) -> {
                    send.append("   " + name + " : " + description + "\n");
                });
                event.getChannel().sendMessage(send.toString()).complete();
            } else if (Commands.containsKey(cmd)) {
                Commands.get(cmd).accept(event);
            } else {
                event.getChannel().sendMessage("Command not found :'( ");
            }
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        event.getGuild().getPublicChannel().sendMessage("Good bye " + event.getMember().getNickname() + ",you little son of a bitch <3 ");
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        event.getGuild().getPublicChannel().sendMessage("Welcome " + event.getMember().getNickname() + " <3 ");
    }
/*
    @Override
    public void onPlayerGainedPP(GainedPPEvent event) {
        final OSUPlayer player = event.getPlayer();

        try {
            RecentPlay play = player.getRecentPlay();
            BeatMap map = HAEN.osu.getBeatMap(play.getBeatMapID());



        players.get(event.getPlayer().getUserID()).forEach(element -> {
            try {
                EmbedBuilder ms = new EmbedBuilder();
                ms.setThumbnail(player.getAvatarUrl());
                ms.setImage(osuURL(getURLContent("https://osu.ppy.sh/b/"+map.getBeatMapID())));
                ms.setDescription("__"+map.getTitle()+" ~ "+map.getArtist()+" "+map.getDiffRating() +"* :"+event.getPPGainedPretty()+"pp __");
                ms.setTitle(player.getUsername(),player.getProfileUrl());
                ms.addField("Combo","x"+play.getCombo(),true);
                ms.addField("Score",""+play.getScore(),true);
                ms.addField("MAX/300/200/100/50/MISS",play.getCountGeki()+"/"+play.getCount300()+"/"+play.getCountKatu()+"/"+play.getCount100()+"/"+play.getCount50()+"/"+play.getCountMiss(),true);
                ms.addField("Mods", play.getMods(),true);
            //MessageEmbedImpl ms = new MessageEmbedImpl().setColor(Color.LIGHT_GRAY).setAuthor(new MessageEmbed.AuthorInfo(player.getUsername(), player.getProfileUrl(), player.getAvatarUrl(), "a.ppy.sh"))
              //      .setDescription("__"+map.getTitle()+" ~ "+map.getArtist()+":"+event.getPPGainedPretty()+"pp __\n○ *"+play.getCombo()+" ¤ "++" ¤ "+play.getScore()+"\n○ "+
                //    play.getMods()+" ¤ "+map.getDiffRating())
                 //   .setImage(new MessageEmbed.ImageInfo(,"https://b.ppy.sh/",166,120));
                     element.sendMessage(ms.build());
            } catch (Exception e) {

            }

        });
        } catch (RestfulException e) {

        }
    }*/
    private String getURLContent(String url) throws Exception{
        URL x = new URL(url);
        URLConnection con = x.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String l;
        String result ="";
        while ((l=in.readLine())!=null) {
            result+=l;
        }


        return result;
    }

    private String osuURL(String content){
        for(String a:content.split("[\\\"\\\"]")){
            if(a.contains("b.ppy.sh")){
                return a;
            }
        }
        return "";
    }
}
