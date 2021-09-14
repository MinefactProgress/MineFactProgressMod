package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import de.jannik0308.minefactprogressmod.utils.api.APIRequestHandler;
import de.jannik0308.minefactprogressmod.utils.api.JSONBuilder;
import de.jannik0308.minefactprogressmod.utils.chat.ChatColor;
import de.jannik0308.minefactprogressmod.utils.chat.MessageBuilder;
import de.jannik0308.minefactprogressmod.utils.scanmap.Coordinate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientChat {

    public static final ArrayList<Coordinate> coordinates = new ArrayList<>();

    private boolean isUploading = false;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onChat(ClientChatEvent e) {
        LocalPlayer p = Minecraft.getInstance().player;
        String msg = e.getMessage();
        if(msg.startsWith("/progress")) {
            //Check if on BTE.net
            if(!ProgressUtils.isOnBTEnet()) {
                return;
            }
            e.setCanceled(true);
            // TODO
            // Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(msg);
            Minecraft.getInstance().gui.getChat().addRecentChat(msg);
            //Check if on New York City Server
            if(!ProgressUtils.getConnectedBTEServer().equals("NewYorkCity")) {
                ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "You can only use this command on the New York City Server");
                return;
            }
            String[] args = msg.replace("/progress ", "").split(" ");

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
                            // TODO
                            //p.sendChatMessage("/ll");
                            p.chat("/ll");
                        }
                        return;
                    }
                    if(args[1].equalsIgnoreCase("complete")) {
                        if(isUploading) {
                            ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "You are currently uploading an area. Please wait until its finished");
                            return;
                        }
                        if(coordinates.size() < 3) {
                            ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "You need to select at least 3 points");
                            return;
                        }
                        new Thread(() -> {
                            isUploading = true;
                            ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GRAY + "New area uploading...");

                            //Check ID
                            HashMap<String, Object> map = APIRequestHandler.doGETRequest(MineFactProgressMod.WEBSITE + "api/scanmap/get?token=dev");
                            if(map == null) {
                                ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.RED + "Error while uploading. Please try again!");
                                return;
                            }

                            //Get next ID
                            ArrayList<Object> areas = (ArrayList<Object>) map.get("areas");
                            int id = -1;
                            for (Object area : areas) {
                                JSONBuilder jsonBuilder = new JSONBuilder(area.toString());
                                int currentID = (int) jsonBuilder.toHashMap().get("areaID");
                                if (currentID > id) {
                                    id = currentID;
                                }
                            }

                            Coordinate[] coordsArray = new Coordinate[coordinates.size()];
                            for(int i = 0; i < coordinates.size(); i++) {
                                coordsArray[i] = coordinates.get(i);
                            }

                            JSONBuilder jsonBuilder = new JSONBuilder();
                            jsonBuilder.put("token", "dev");
                            jsonBuilder.put("id", id);
                            jsonBuilder.put("points", coordsArray);

                            APIRequestHandler.doPOSTRequest("https://gefsn.sse.codesandbox.io/api/scanmap/add", jsonBuilder);

                            ProgressUtils.sendPlayerMessage(MineFactProgressMod.PREFIX + ChatColor.GREEN + "Area " + ChatColor.YELLOW + "#" + id + ChatColor.GREEN + " created successfully");
                            coordinates.clear();
                            isUploading = false;
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
