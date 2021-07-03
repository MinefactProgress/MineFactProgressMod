package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import de.jannik0308.minefactprogressmod.utils.api.APIRequestHandler;
import de.jannik0308.minefactprogressmod.utils.api.JSONBuilder;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ClientChatReceived {

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent e) {
        if(!ProgressUtils.isOnBTEnet()) return;

        String msg = e.getMessage().getString();
        ClientPlayerEntity p = Minecraft.getInstance().player;
        String connectedServer = ProgressUtils.getConnectedServer();

        //Building Server NYC
        if(connectedServer.startsWith("Building") && connectedServer.contains("NYC")) {
            //Set Project Count
            if (msg.startsWith("Total Finished Projects: ")) {
                String countStr = msg.replace("Total Finished Projects: ", "");
                try {
                    int count = Integer.parseInt(countStr);
                    setProjectsAsync(count, p);
                    setLeaderboardAsync(p);
                } catch (NumberFormatException ignored) {
                }
            }
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

    private void setLeaderboardAsync(ClientPlayerEntity p) {
        if(p == null) return;

        //Async Thread
        Thread thread = new Thread(() -> {
            List<Entity> entities = getArmorStands(p.world, p.getPosX(), p.getPosZ(), 20);
            if(entities.isEmpty()) return;

            if(entities.get(0).getName().getString().contains("LIFETIME")) {
                try {
                    Thread.sleep(10000);
                    setLeaderboardAsync(p);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(entities.get(0).getName().getString().contains("MONTHLY")) {
                String[] names = new String[5];

                for(int i = 3; i <= 7; i++) {
                    String[] parts = StringUtils.stripControlCodes(entities.get(i).getName().getString()).split("-");
                    // Add names
                    String name = parts[1].replace(" ", "").replace("(!)", "");

                    // Add points
                    String temp = parts[2].replace(" ", "");
                    StringBuilder points = new StringBuilder();
                    for(int j = 0; j < temp.length(); j++) {
                        if(temp.charAt(j) == '|') break;
                        points.append(temp.charAt(j));
                    }

                    names[i-3] = name + " | " + points.toString().replace("Points", " Points");
                }

                //Build JSON
                JSONBuilder json = new JSONBuilder();
                json.put("token", "dev");
                json.put("first", names[0]);
                json.put("seccond", names[1]);
                json.put("third", names[2]);
                json.put("fourth", names[3]);
                json.put("fifth", names[4]);

                //API Request
                APIRequestHandler.doPOSTRequest("https://gefsn.sse.codesandbox.io/api/event/leaderboard/set", json);

                //Send Message to Player
                ITextComponent text = new StringTextComponent(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Leaderboard synchronized");
                p.sendMessage(text, Util.DUMMY_UUID);
            }
        });
        thread.start();
    }

    private List<Entity> getArmorStands(World w, double x, double z, int radius) {
        return w.getEntitiesWithinAABB(ArmorStandEntity.class, new AxisAlignedBB(x,0,z,x+1,257,z+1).grow(radius));
    }
}
