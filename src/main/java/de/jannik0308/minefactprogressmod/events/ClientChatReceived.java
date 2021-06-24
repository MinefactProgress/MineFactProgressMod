package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.DiscordWebhook;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.io.IOException;

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
            } catch (NumberFormatException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setProjects(int projects) throws IOException {
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/857708669571170344/-KhCDHtUp6ECZAtumEjsUiUlf6sg8DsmYCq_dbM047ifRpENjOwroOPGWvzhh8jGVEYn");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setTitle("Current Project Count")
            .setDescription("/projects list got executed and the current project count is **" + projects + "**!")
            .setColor(Color.CYAN));
        webhook.execute();
    }
}
