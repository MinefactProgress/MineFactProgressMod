package de.jannik0308.minefactprogressmod.events;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientChatReceived {

    @SubscribeEvent
    public void onCommand(ClientChatReceivedEvent e) {
        String msg = e.getMessage().getString();
        if(msg.startsWith("Total Finished Projects: ")) {
            System.out.println("Message starts with right words");
            System.out.println(msg.split(":")[1]);
        }
    }
}
