package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.api.APIRequestHandler;
import de.jannik0308.minefactprogressmod.utils.api.JSONBuilder;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
                setProjectsAsync(count, p);
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setProjectsAsync(int projects, ClientPlayerEntity p) {
        //POST Request to API
        Thread thread = new Thread(() -> {
            //Build JSON
            JSONBuilder json = new JSONBuilder();
            json.put("token", "dev");
            json.put("projects", projects);

            //API Request
            APIRequestHandler.doPOSTRequest("https://gefsn.sse.codesandbox.io/api/projects/edit", json);
            if(p != null) {
                ITextComponent text = new StringTextComponent(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Projects set to " + ChatColor.YELLOW + projects);
                p.sendMessage(text, Util.DUMMY_UUID);
            }
        });
        thread.start();
    }
}
