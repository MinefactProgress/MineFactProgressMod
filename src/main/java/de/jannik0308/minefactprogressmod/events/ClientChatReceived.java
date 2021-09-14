package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import de.jannik0308.minefactprogressmod.utils.api.APIRequestHandler;
import de.jannik0308.minefactprogressmod.utils.api.JSONBuilder;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import de.jannik0308.minefactprogressmod.utils.scanmap.Coordinate;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class ClientChatReceived {

    public static int addingPointsCounter = 0;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent e) {
        if(!ProgressUtils.isOnBTEnet()) return;

        String msg = e.getMessage().getString();
        String msgWithoutColorCodes = StringUtil.stripColor(msg);
        LocalPlayer p = Minecraft.getInstance().player;
        String connectedServer = ProgressUtils.getConnectedBTEServer();

        //Check if player is adding a point for the scanmap
        if(addingPointsCounter > 0) {
            e.setCanceled(true);

            if(msgWithoutColorCodes.startsWith("Latitude: ")) {
                String latlong = msgWithoutColorCodes.replace("Latitude: ", "")
                        .replace(" Longitude: ", "")
                        .replace(" (Click to copy)", "");
                Coordinate coord = new Coordinate(latlong.split(",")[0], latlong.split(",")[1]);
                ClientChat.coordinates.add(coord);
                ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Added point " + ChatColor.YELLOW + "#" + ClientChat.coordinates.size() + ChatColor.GRAY + " to the selection (" + ChatColor.YELLOW + latlong + ChatColor.GRAY + ")");
            }
            addingPointsCounter--;
        }

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

    private void setProjectsAsync(int projects, LocalPlayer p) {
        //POST Request to API
        Thread thread = new Thread(() -> {
            //Build JSON
            JSONBuilder json = new JSONBuilder();
            json.put("token", "dev");
            json.put("projects", projects);

            //API Request
            APIRequestHandler.doPOSTRequest("https://gefsn.sse.codesandbox.io/api/projects/edit", json);
            if(p != null) {
                TextComponent text = new TextComponent(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Projects set to " + ChatColor.YELLOW + projects);
                p.sendMessage(text, Util.NIL_UUID);
            }
        });
        thread.start();
    }

    private void setLeaderboardAsync(LocalPlayer p) {
        if(p == null) return;

        //Async Thread
        Thread thread = new Thread(() -> {
            List<ArmorStand> entities = getArmorStands(p.level, p.getX(), p.getZ(), 20);
            if(entities.isEmpty()) return;

            if(entities.get(0).getName().getString().contains("LIFETIME")) {
                try {
                    Thread.sleep(10000);
                    setLeaderboardAsync(p);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if(entities.get(0).getName().getString().contains("WEEKLY")) {
                String[] names = new String[5];

                for(int i = 3; i <= 7; i++) {
                    String[] parts = StringUtil.stripColor(entities.get(i).getName().getString()).split("-");
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
                TextComponent text = new TextComponent(MineFactProgressMod.PREFIX + ChatColor.GRAY + "Leaderboard synchronized");
                p.sendMessage(text, Util.NIL_UUID);
            }
        });
        thread.start();
    }

    private List<ArmorStand> getArmorStands(Level world, double x, double z, int radius) {
        return world.getEntitiesOfClass(ArmorStand.class, new AABB(x,0,z,x+1,257,z+1).inflate(radius));
    }
}
