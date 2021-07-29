package de.jannik0308.minefactprogressmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ProgressUtils {

    public static ClientPlayerEntity getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void sendPlayerMessage(String msg) {
        ClientPlayerEntity p = Minecraft.getInstance().player;
        if(p != null) {
            ITextComponent text = new StringTextComponent(msg);
            p.sendMessage(text, Util.DUMMY_UUID);
        }
    }

    public static boolean isOnBTEnet() {
        if(Minecraft.getInstance().isSingleplayer()) return false;

        return getTablistHeader() != null && getTablistHeader().contains("BuildTheEarth.net");
    }

    public static boolean isBuildingSession() {
        if(!getConnectedBTEServer().equals("NewYorkCity")) return false;

        return getPlayer().getHeldItemMainhand().getDisplayName().getString().contains("Session Manager");
    }

    public static String getTablistHeader() {
        //Access private header field
        PlayerTabOverlayGui tablist = Minecraft.getInstance().ingameGUI.getTabList();
        Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlayGui.class, "field_175256_i");
        try {
            ITextComponent text = (ITextComponent) f.get(tablist);
            List<ITextComponent> siblings = text.getSiblings();

            return siblings.get(0).getString();
        } catch (IllegalAccessException | NullPointerException e) {
            return null;
        }
    }

    public static String getConnectedBTEServer() {
        if(!isOnBTEnet() || getTablistHeader() == null) return "";

        String connectedServer = getTablistHeader().replace(" ", "")
                                                   .replace("BuildTheEarth.net", "");
        connectedServer = StringUtils.stripControlCodes(connectedServer);
        return connectedServer.split("\\|")[0];
    }

}
