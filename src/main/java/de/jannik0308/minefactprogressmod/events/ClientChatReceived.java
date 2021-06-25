package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.DiscordWebhook;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;
import java.io.IOException;

public class ClientChatReceived {

    @SubscribeEvent
    public void onCommand(ClientChatReceivedEvent e) {
        String msg = e.getMessage().getString();
        ClientPlayerEntity p = Minecraft.getInstance().player;

        //Set Project Count
        if(msg.startsWith("Total Finished Projects: ")) {
            String countStr = msg.replace("Total Finished Projects: ", "");
            try {
                int count = Integer.parseInt(countStr);
                MineFactProgressMod.LOGGER.info(count);
                if(p != null) {
                    ITextComponent text = new StringTextComponent(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Projects set to " + ChatColor.YELLOW + count);
                    p.sendMessage(text, Util.DUMMY_UUID);
                }
                setProjects(count);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setProjects(int projects) throws IOException {
        //Send Discord Webhook
        DiscordWebhook webhook = new DiscordWebhook("https://discord.com/api/webhooks/857708669571170344/-KhCDHtUp6ECZAtumEjsUiUlf6sg8DsmYCq_dbM047ifRpENjOwroOPGWvzhh8jGVEYn");
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
            .setTitle("Current Project Count")
            .setDescription("/projects list got executed and the current project count is **" + projects + "**!")
            .setColor(Color.CYAN));
        webhook.execute();
    }
}
