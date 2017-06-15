import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Yves on 13/06/2017.
 */
public class Command extends ListenerAdapter{
    private HashMap<String,Consumer<MessageReceivedEvent>> Commands;
    private HashMap<String,String> description;
    static String commandCall;
    public static HashMap<String, Thread> runningThreads = new HashMap<>();

    public int scanLimit = 5;

    public Command() {
        this.Commands= new HashMap<String,Consumer<MessageReceivedEvent>>();
        this.description = new HashMap<String,String>();
        runningThreads= new HashMap<String,Thread>();
        commandCall = "$";
    }
    public void addCommand(Consumer<MessageReceivedEvent> command,String name,String de){
        Commands.put(name, command);
        description.put(name,de);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg=event.getMessage().getContent();
        if(msg.startsWith(commandCall)&&msg.length()>2){
            String cmd=msg.substring(1,msg.contains(" ")?msg.indexOf(" "):msg.length());
            if(cmd.contentEquals("help")){
                final StringBuilder send= new StringBuilder("Help command list: \n");
                description.forEach((name,description) -> {
                    send.append("   "+name+" : "+description+"\n");
                });
                event.getChannel().sendMessage(send.toString()).complete();
            }
            else if(Commands.containsKey(cmd)){
                Commands.get(cmd).accept(event);
            }
            else{
                event.getChannel().sendMessage("Command not found :'( ");
            }
        }
    }
}
