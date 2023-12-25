package com.github.nitalerie.ghostblocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

@Mod(modid = "ghostblocks", useMetadata=true)
public class GhostBlocks {

    private Minecraft mc = Minecraft.getMinecraft();

    private final KeyBinding createGhostKey = new KeyBinding("Create Ghost Blocks", 0, "Ghost Blocks");
    private final KeyBinding resetGhostsKey = new KeyBinding("Reset All Ghost Blocks", 0, "Ghost Blocks");

    private final ArrayList<Pair<BlockPos, IBlockState>> ghostBlocks = new ArrayList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(createGhostKey);
        ClientRegistry.registerKeyBinding(resetGhostsKey);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (mc.currentScreen != null) return;
        WorldClient worldClient = mc.theWorld;
        if (createGhostKey.isKeyDown() && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            ghostBlocks.add(Pair.of(mc.objectMouseOver.getBlockPos(), worldClient.getBlockState(mc.objectMouseOver.getBlockPos())));
            worldClient.setBlockToAir(mc.objectMouseOver.getBlockPos());
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if (mc.currentScreen != null) return;
        if (!resetGhostsKey.isPressed()) return;
        WorldClient worldClient = mc.theWorld;

        for (Pair<BlockPos, IBlockState> block : ghostBlocks) {
            worldClient.setBlockState(block.getLeft(), block.getRight());
        }
        ghostBlocks.clear();

        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Reset all ghost blocks."));
    }

}
