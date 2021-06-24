package de.jannik0308.minefactprogressmod.events;

import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerExecuteCommandEvent {

    @SubscribeEvent
    public void onCommand(CommandEvent e) {
        System.out.println("Command executed!");
    }
}
