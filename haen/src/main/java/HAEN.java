import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Yves on 13/06/2017.
 */
public class HAEN {
    JDA haen;
    public HAEN(String token) throws LoginException,RateLimitedException,InterruptedException{
        this.haen = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        Command Command = new Command();
        Command.addCommand(event->{

                String classToPlay = event.getMessage().getContent().split(" ")[1];
                HashMap<String,String> javaContent= new HashMap<String,String>();
                HashMap<String,File> files = new HashMap<String,File>();
                for (String s : event.getMessage().getContent().replaceAll("```","¤").split("[\\¤\\¤]")) {
                    if(s.length()>13){
                        javaContent.put(s.substring(s.indexOf("class")+5,s.indexOf(s.contains("extends")?"extends":s.contains("implements")?"implements":"{")).replaceAll(" ",""),s);
                    }
                }
            if(javaContent.containsKey(classToPlay)){
                Thread thread = new Thread(new Runnable() {
                    Process p;

                    public void run() {
                        try {
                            javaContent.forEach((name,content)->{
                                        try{
                                            System.out.println("");
                                            FileWriter f=new FileWriter("./"+name+".java");
                                            f.write(content);
                                            f.flush();
                                            f.close();
                                            files.put(name, new File("./"+name+".java"));
                                        }catch (Exception e) {
                                            return;
                                        }
                                    }

                            );
                            files.forEach((name,file)->{
                                try{
                                    String msg ="";
                                    String line;
                                    p = Runtime.getRuntime().exec("javac "+file.getAbsolutePath());
                                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));


                                    while ((line = input.readLine()) != null) {
                                        msg += line.length()>0?line+"\n":"";
                                    }
                                    if(msg.length()>0){
                                        event.getChannel().sendMessage("```"+msg+"```").complete();
                                    }
                                }catch (Exception e) {
                                    return;
                                }
                            });
                            String msg ="";
                            String line;
                            p = Runtime.getRuntime().exec("java " + classToPlay);
                            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            while ((line = input.readLine()) != null) {
                                msg += line.length()>0?line+"\n":"";
                            }

                            if(msg.length()>0){
                                event.getChannel().sendMessage("```"+msg+"```").complete();
                            }
                            files.forEach((name,file)->{
                                file.delete();
                                new File("./"+name+".class").delete();
                            });
                        }catch (Exception e) {
                            return;
                        }
                    }
                });
                thread.run();
            }
            else{
                event.getChannel().sendMessage("Incorrect command pattern.");
            }


                },"java", "Compile java code: $java [main name class] [Complete file content between three '`' at the beginning and at the end like (xxxx).]...\n          Exemple: $java HelloWorld ```public class HelloWorld{\n" +
                "\t\tpublic static void main(String[] args){\n          " +
                "\t\t\tSystem.out.println(\"Hello world\");\n" +
                "\t\t}\n" +
                "\t}``` "

        );



        haen.addEventListener(Command);
    }
}
