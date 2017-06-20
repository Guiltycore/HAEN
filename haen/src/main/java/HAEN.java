

import com.sethsutopia.utopiai.restful.RestfulException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import com.sethsutopia.utopiai.osu.OSU;
import com.sethsutopia.utopiai.osu.*;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by Yves on 13/06/2017.
 */
public class HAEN {
    JDA haen;
    static OSU osu;

    public HAEN(String token,String osuToken) throws LoginException, RateLimitedException, InterruptedException {
        osu = new OSU(osuToken);
        this.haen = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        Command Command = new Command();
        //Java Compiler
        Command.addCommand(event -> {

                    String classToPlay = event.getMessage().getContent().split(" ")[1];
                    HashMap<String, String> javaContent = new HashMap<String, String>();
                    HashMap<String, File> files = new HashMap<String, File>();
                    for (String s : event.getMessage().getContent().replaceAll("```", "¤").split("[\\¤\\¤]")) {
                        if (s.length() > 13) {
                            javaContent.put(s.substring(s.indexOf("class") + 5, s.indexOf(s.contains("extends") ? "extends" : s.contains("implements") ? "implements" : "{")).replaceAll(" ", ""), s);
                        }
                    }
                    if (javaContent.containsKey(classToPlay)) {
                        Thread thread = new Thread(new Runnable() {
                            Process p;

                            public void run() {
                                try {
                                    javaContent.forEach((name, content) -> {
                                                try {
                                                    System.out.println("");
                                                    FileWriter f = new FileWriter("./" + name + ".java");
                                                    f.write(content);
                                                    f.flush();
                                                    f.close();
                                                    files.put(name, new File("./" + name + ".java"));
                                                } catch (Exception e) {
                                                    return;
                                                }
                                            }

                                    );
                                    files.forEach((name, file) -> {
                                        try {
                                            String msg = "";
                                            String line;
                                            p = Runtime.getRuntime().exec("javac " + file.getAbsolutePath());
                                            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));


                                            while ((line = input.readLine()) != null) {
                                                msg += line.length() > 0 ? line + "\n" : "";
                                            }
                                            if (msg.length() > 0) {
                                                event.getChannel().sendMessage("```" + msg + "```").complete();
                                            }
                                        } catch (Exception e) {
                                            return;
                                        }
                                    });
                                    String msg = "";
                                    String line;
                                    p = Runtime.getRuntime().exec("java " + classToPlay);
                                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                                    while ((line = input.readLine()) != null) {
                                        msg += line.length() > 0 ? line + "\n" : "";
                                    }

                                    if (msg.length() > 0) {
                                        event.getChannel().sendMessage("```" + msg + "```").complete();
                                    }
                                    files.forEach((name, file) -> {
                                        file.delete();
                                        new File("./" + name + ".class").delete();
                                    });
                                } catch (Exception e) {
                                    return;
                                }
                            }
                        });
                        thread.run();
                    } else {
                        event.getChannel().sendMessage("Incorrect command pattern.");
                    }


                }, "java", "Compile java code: $java [main name class] [Complete file content between three '`' at the beginning and at the end like (xxxx).]...\n          *Exemple:* $java HelloWorld ```public class HelloWorld{\n" +
                        "\t\tpublic static void main(String[] args){\n          " +
                        "\t\t\tSystem.out.println(\"Hello world\");\n" +
                        "\t\t}\n" +
                        "\t}``` "
        );


        //Scan
        Command.addCommand(event -> {
            if (Command.runningThreads.containsKey(event.getGuild().getId())) {
                event.getChannel().sendMessage("1 scan per server please.").complete();
            } else if (Command.runningThreads.size() >= Command.scanLimit) {
                event.getChannel().sendMessage("Too much scans are running. Please wait. My little MADAFAKA <3").complete();
            } else if (event.getMessage().getContent().length() <= 6) {
                event.getChannel().sendMessage("Please put in a host ").complete();
            } else {
                Thread thread = new Thread(new Runnable() {

                    Process p;

                    public void run() {
                        try {
                            p = Runtime.getRuntime().exec("sudo nmap -v -A -p- " + event.getMessage().getContent().replace(Command.commandCall + "scan", "").replace(" ", ""));
                            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String line;
                            while ((line = input.readLine()) != null) {
                                event.getChannel().sendMessage("[Scan] > " + line).complete();
                            }
                            input.close();
                            Command.runningThreads.remove(event.getGuild().getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (p != null && p.isAlive()) p.destroy();
                            Command.runningThreads.remove(event.getGuild().getId());
                            return;
                        }
                    }
                });
                Command.runningThreads.put(event.getGuild().getId(), thread);
                thread.run();
            }
        }, "scan", "Displays differents open ports from an IP address. $scan [IP] \n          *Exemple:* $scan 127.0.0.1");

        //OsuPlayerDisplay

        Command.addCommand(event -> {
            OSUPlayer player =null;
            String[]  command =event.getMessage().getContent().split(" ");
            String msg = event.getMessage().getContent();
                try{
                    if(msg.contains("mania"))
                        player=osu.getUser(command[1], OSU.OsuGameMode.MANIA);
                    else if(msg.contains("std"))
                        player=osu.getUser(command[1],OSU.OsuGameMode.OSU);
                    else if(msg.contains("taiko"))
                        player=osu.getUser(command[1],OSU.OsuGameMode.TAIKO);
                    else if(msg.contains("ctb"))
                        player=osu.getUser(command[1],OSU.OsuGameMode.CATCHTHEBEAT);
                    if(player!=null) {
                        EmbedBuilder ms = new EmbedBuilder();
                        ms.setTitle(player.getUsername(),player.getProfileUrl());
                        ms.setColor(Color.CYAN);
                        ms.setThumbnail(player.getAvatarUrl());
                        ms.addField(new MessageEmbed.Field("Country",player.getCountry(),true));
                        ms.addField(new MessageEmbed.Field("Country rank",""+player.getPPCountryRank(),true));
                        ms.addField(new MessageEmbed.Field("Rank", ""+player.getPPRank(),true));
                        ms.addField(new MessageEmbed.Field("PP",""+ player.getPPRaw(),true));
                        ms.addField(new MessageEmbed.Field("Accuracy",""+ player.getAccPretty(),true));
                        ms.addField(new MessageEmbed.Field("Level",""+player.getLevel(),true));

                        event.getChannel().sendMessage(ms.build()).complete();

                    }

                }catch (RestfulException e){
                        event.getChannel().sendMessage("a").complete();

                }



        },"osu","Displays player informations about a osu player. $osu [playername] {std|taiko|mania|ctb}\n          *Exemple:* $osu WiwiTriggeredMe std");

        haen.addEventListener(Command);
    }
}
