package de.jannik0308.minefactprogressmod.events;

import de.jannik0308.minefactprogressmod.config.SettingsConfig;
import de.jannik0308.minefactprogressmod.utils.ProgressUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LoggedIn {

    @SubscribeEvent
    public void onJoin(ClientPlayerNetworkEvent.LoggedInEvent e) {
        if(Minecraft.getInstance().player != null) {
            ClientPlayerEntity p = Minecraft.getInstance().player;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    if(ProgressUtils.getConnectedBTEServer().equals("NewYorkCity")) {
                        if(ProgressUtils.isBuildingSession()) {
                            p.sendChatMessage("/fly");
                        }
                        if(SettingsConfig.enableAutoGamemode.get()) {
                            p.sendChatMessage("/gamemode creative");
                        }
                        if(SettingsConfig.autoSpeedValue.get() != 1) {
                            p.sendChatMessage("/speed " + SettingsConfig.autoSpeedValue.get());
                        }
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();

        }
    }
}
