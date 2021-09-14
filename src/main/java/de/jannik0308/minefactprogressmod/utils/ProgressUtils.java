package de.jannik0308.minefactprogressmod.utils;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ProgressUtils {

    public static LocalPlayer getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void sendPlayerMessage(String msg) {
        LocalPlayer p = Minecraft.getInstance().player;
        if(p != null) {
            TextComponent text = new TextComponent(msg);
            p.sendMessage(text, Util.NIL_UUID);
        }
    }

    public static boolean isOnBTEnet() {
        if(Minecraft.getInstance().hasSingleplayerServer()) return false;

        return getTablistHeader() != null && getTablistHeader().contains("BuildTheEarth.net");
    }

    public static boolean isBuildingSession() {
        if(!getConnectedBTEServer().equals("NewYorkCity")) return false;

        return getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getDisplayName().getString().contains("Session Manager");
    }

    public static String getTablistHeader() {
        //Access private header field
        //TODO
        //PlayerTabOverlayGui tablist = Minecraft.getInstance().ingameGUI.getTabList();
        PlayerTabOverlay tablist = Minecraft.getInstance().gui.getTabList();

        // TODO Check field name
        Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlay.class, "f_94522_");
        try {
            // TODO
            //ITextComponent text = (ITextComponent) f.get(tablist);
            TextComponent text = (TextComponent) f.get(tablist);
            List<Component> siblings = text.getSiblings();

            return siblings.get(0).getString();
        } catch (IllegalAccessException | NullPointerException e) {
            return null;
        }
    }

    public static String getConnectedBTEServer() {
        if(!isOnBTEnet() || getTablistHeader() == null) return "";

        String connectedServer = getTablistHeader().replace(" ", "")
                                                   .replace("BuildTheEarth.net", "");
        // TODO
        //connectedServer = StringUtils.stripControlCodes(connectedServer);
        connectedServer = StringUtil.stripColor(connectedServer);

        return connectedServer.split("\\|")[0];
    }

}
