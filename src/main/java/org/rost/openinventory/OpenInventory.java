package org.rost.openinventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = OpenInventory.MODID, name = "Open Inventory", version = "1.0")
public class OpenInventory
{
    public static final double MAX_DISTANCE = 1.75;
    private static final Map<EntityPlayer, EntityPlayer> openInventories = new HashMap<>();
    public static final String MODID = "openinventory";

    public OpenInventory() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent.EntityInteract event) {
        EntityPlayer interactingPlayer = event.getEntityPlayer();
        if (!(event.getTarget() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer targetPlayer = (EntityPlayer) event.getTarget();
        if (!interactingPlayer.world.isRemote) {
            double distance = interactingPlayer.getDistance(targetPlayer);
            if (distance <= MAX_DISTANCE) {
                interactingPlayer.displayGUIChest(targetPlayer.inventory);
                openInventories.put(interactingPlayer, targetPlayer);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (openInventories.containsKey(player)) {
            EntityPlayer targetPlayer = openInventories.get(player);
            double distance = player.getDistance(targetPlayer);
            if (distance > MAX_DISTANCE) {
                player.closeScreen();
                openInventories.remove(player);
            }
        }
    }

    @SubscribeEvent
    public void onInventoryClose(PlayerContainerEvent.Close event) {
        EntityPlayer player = event.getEntityPlayer();
        if (openInventories.containsKey(player)) {
            openInventories.remove(player);
        }
    }
}