package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientChatReceived {

    @SubscribeEvent
    public void onCommand(ClientChatReceivedEvent e) {
        String msg = e.getMessage().getString();
        MineFactProgressMod.LOGGER.info("Chat Message Received");

        //Set Project Count
        if(msg.startsWith("Total Finished Projects: ")) {
            MineFactProgressMod.LOGGER.info("Debug: Message starts with right words");
            String countStr = msg.replace("Total Finished Projects: ", "");
            try {
                int count = Integer.parseInt(countStr);
                MineFactProgressMod.LOGGER.info(count);
                setProjects(count);
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setProjects(int projects) {

    }
}
