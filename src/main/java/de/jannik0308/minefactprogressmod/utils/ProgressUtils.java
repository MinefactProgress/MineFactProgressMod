package de.jannik0308.minefactprogressmod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ProgressUtils {

    public static boolean isOnBTEnet() {
        if(Minecraft.getInstance().isSingleplayer()) return false;


        return getTablistHeader() != null && getTablistHeader().contains("BuildTheEarth.net");
    }

    public static String getTablistHeader() {
        //Access private header field
        PlayerTabOverlayGui tablist = Minecraft.getInstance().ingameGUI.getTabList();
        Field f = ObfuscationReflectionHelper.findField(PlayerTabOverlayGui.class, "field_175256_i");
        try {
            ITextComponent text = (ITextComponent) f.get(tablist);
            List<ITextComponent> siblings = text.getSiblings();

            return siblings.get(0).getString();
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static String getConnectedBTEServer() {
        if(getTablistHeader() == null || !isOnBTEnet()) return "";

        String connectedServer = getTablistHeader().replace(" ", "")
                                                   .replace("BuildTheEarth.net", "");
        connectedServer = StringUtils.stripControlCodes(connectedServer);
        return connectedServer.split("\\|")[0];
    }

}
