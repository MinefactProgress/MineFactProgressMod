package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import de.jannik0308.minefactprogressmod.utils.api.APIRequestHandler;
import de.jannik0308.minefactprogressmod.utils.api.JSONBuilder;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import de.jannik0308.minefactprogressmod.utils.chat.MessageBuilder;
import de.jannik0308.minefactprogressmod.utils.scanmap.Coordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientChat {

    public static final ArrayList<Coordinate> coordinates = new ArrayList<>();

    @SubscribeEvent
    public void onChat(ClientChatEvent e) {
        ClientPlayerEntity p = Minecraft.getInstance().player;
        String msg = e.getMessage();
        if(msg.startsWith("/progress")) {
            //Check if on BTE.net
            if(!ProgressUtils.isOnBTEnet()) {
                return;
            }
            e.setCanceled(true);
            //Check if on New York City Server
            if(!ProgressUtils.getConnectedBTEServer().equals("NewYorkCity")) {
                ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "You can only use this command on the New York City Server");
                return;
            }
            String[] args = msg.replace("/progress ", "").split(" ");
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(msg);

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("help")) {
                    sendHelpMessage(1);
                    return;
                }
                if(args[0].equalsIgnoreCase("config")) {
                    ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GRAY + "To edit the config file open your "
                            + ChatColor.YELLOW +  ".minecraft/config folder" + ChatColor.GRAY + ". There you can find the file "
                            + ChatColor.YELLOW + "MineFact-Progress.toml" + ChatColor.GRAY + ". In there you can edit the values of different things");
                    return;
                }
            }
            if(args.length == 2) {
                if(args[0].equalsIgnoreCase("help")) {
                    try {
                        sendHelpMessage(Integer.parseInt(args[1]));
                    } catch (NumberFormatException ex) {
                        ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "Please enter a valid number");
                    }
                    return;
                }
                if(args[0].equalsIgnoreCase("scanmap")) {
                    if(args[1].equalsIgnoreCase("clearselection")) {
                        coordinates.clear();
                        ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GRAY + "List of points for current area cleared");
                        return;
                    }
                    if(args[1].equalsIgnoreCase("addpoint")) {
                        if(p != null) {
                            ClientChatReceived.addingPointsCounter = 5;
                            p.sendChatMessage("/ll");
                        }
                        return;
                    }
                    if(args[1].equalsIgnoreCase("complete")) {
                        new Thread(() -> {
                            Coordinate[] coordsArray = new Coordinate[coordinates.size()];
                            for(int i = 0; i < coordinates.size(); i++) {
                                coordsArray[i] = coordinates.get(i);
                            }

                            JSONBuilder jsonBuilder = new JSONBuilder();
                            jsonBuilder.put("token", "dev");
                            jsonBuilder.put("points", coordsArray);

                            APIRequestHandler.doPOSTRequest("https://gefsn.sse.codesandbox.io/api/scanmap/add", jsonBuilder);

                            ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GREEN + "New area created successfully");
                            coordinates.clear();
                        }).start();
                        return;
                    }
                }
            }
            sendHelpMessage(1);
        }
    }

    private void sendHelpMessage(int page) {
        List<String> msg = new ArrayList<>();
        msg.add(ChatColor.YELLOW + "/progress help <Page> " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Display a specific help page");
        msg.add(ChatColor.YELLOW + "/progress scanmap addPoint " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Add your current location to the selection");
        msg.add(ChatColor.YELLOW + "/progress scanmap complete " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Add your current selection to the scanmap on the Website");
        msg.add(ChatColor.YELLOW + "/progress scanmap clearSelection " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Clear your current selection");
        msg.add(ChatColor.YELLOW + "/progress config " + ChatColor.DARK_GRAY + "| " + ChatColor.GRAY + "Edit the config file");

        ProgressUtils.sendPlayerMessage(MessageBuilder.getHelpMessage(MineFactProgressMod.PREFIX, ChatColor.AQUA, msg, 8, page));
    }
}
