import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by Yves on 13/06/2017.
 */
public class Command extends ListenerAdapter{
    private HashMap<String,Consumer<MessageReceivedEvent>> Commands;
    private String commandCall;

    public Command() {
        this.Commands= new HashMap<String,Consumer<MessageReceivedEvent>>();
        this.commandCall = "$";
    }
    public void addCommand(Consumer<MessageReceivedEvent> command,String name){
        Commands.put(name, command);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msg=event.getMessage().getContent();
        if(msg.startsWith(commandCall)&&msg.length()>2){
            String cmd=msg.substring(1,msg.contains(" ")?msg.indexOf(" "):msg.length());
            if(Commands.containsKey(cmd)){
                Commands.get(cmd).accept(event);
            }
            else{
                event.getChannel().sendMessage("Command not found :'( ");
            }
        }
    }

}
