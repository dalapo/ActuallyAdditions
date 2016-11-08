/*
 * This file ("RenderLaserRelay.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.blocks.render;


import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.laser.IConnectionPair;
import de.ellpeck.actuallyadditions.api.laser.LaserType;
import de.ellpeck.actuallyadditions.mod.data.PlayerData;
import de.ellpeck.actuallyadditions.mod.data.PlayerData.PlayerSave;
import de.ellpeck.actuallyadditions.mod.items.ItemLaserWrench;
import de.ellpeck.actuallyadditions.mod.items.ItemLaserWrench.WrenchMode;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityLaserRelay;
import de.ellpeck.actuallyadditions.mod.util.AssetUtil;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class RenderLaserRelay extends TileEntitySpecialRenderer{

    private static final float[] COLOR = new float[]{1F, 0F, 0F};
    private static final float[] COLOR_ITEM = new float[]{43F/255F, 158F/255F, 39/255F};
    private static final float[] COLOR_FLUIDS = new float[]{139F/255F, 94F/255F, 1F};

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float par5, int par6){
        if(tile instanceof TileEntityLaserRelay){
            TileEntityLaserRelay relay = (TileEntityLaserRelay)tile;

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if(player != null){
                PlayerSave data = PlayerData.getDataFromPlayer(player);
                WrenchMode mode = WrenchMode.values()[data.theCompound.getInteger("LaserWrenchMode")];
                if(mode != WrenchMode.NO_PARTICLES){
                    ItemStack stack = player.getHeldItemMainhand();
                    if(mode == WrenchMode.ALWAYS_PARTICLES || (stack != null && stack.getItem() instanceof ItemLaserWrench)){
                        ConcurrentSet<IConnectionPair> connections = ActuallyAdditionsAPI.connectionHandler.getConnectionsFor(tile.getPos(), tile.getWorld());
                        if(connections != null && !connections.isEmpty()){
                            for(IConnectionPair pair : connections){
                                if(!pair.doesSuppressRender() && tile.getPos().equals(pair.getPositions()[0])){
                                    BlockPos first = tile.getPos();
                                    BlockPos second = pair.getPositions()[1];
                                    float[] color = relay.type == LaserType.ITEM ? COLOR_ITEM : (relay.type == LaserType.FLUID ? COLOR_FLUIDS : COLOR);

                                    AssetUtil.renderLaser(first.getX()+0.5, first.getY()+0.5, first.getZ()+0.5, second.getX()+0.5, second.getY()+0.5, second.getZ()+0.5, 120, 0.5F, 0.05, color);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
