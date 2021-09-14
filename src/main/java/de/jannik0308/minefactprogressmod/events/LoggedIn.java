package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.MineFactProgressMod;
import de.jannik0308.minefactprogressmod.config.SettingsConfig;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LoggedIn {

    @SubscribeEvent
    public void onJoin(ClientPlayerNetworkEvent.LoggedInEvent e) {
        MineFactProgressMod.LOGGER.info("Logged In");
        if(Minecraft.getInstance().player != null) {
            MineFactProgressMod.LOGGER.info("Logged In Player");
            LocalPlayer p = Minecraft.getInstance().player;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    MineFactProgressMod.LOGGER.info(ProgressUtils.getConnectedBTEServer());
                    if(ProgressUtils.getConnectedBTEServer().equals("NewYorkCity")) {
                        if(ProgressUtils.isBuildingSession()) {
                            //p.sendChatMessage("/fly");
                            //TODO
                            p.chat("/fly");
                        }
                        if(SettingsConfig.enableAutoGamemode.get()) {
                            //p.sendChatMessage("/gamemode creative");
                            //TODO
                            p.chat("/gamemode creative");
                        }
                        if(SettingsConfig.autoSpeedValue.get() != 1) {
                            //p.sendChatMessage("/speed " + SettingsConfig.autoSpeedValue.get());
                            //TODO
                            p.chat("/speed " + SettingsConfig.autoSpeedValue.get());
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();

        }
    }
}
