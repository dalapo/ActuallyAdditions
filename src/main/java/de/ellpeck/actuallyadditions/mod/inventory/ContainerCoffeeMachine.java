/*
 * This file ("ContainerCoffeeMachine.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2017 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.inventory;

import de.ellpeck.actuallyadditions.mod.inventory.slot.SlotItemHandlerUnconditioned;
import de.ellpeck.actuallyadditions.mod.inventory.slot.SlotOutput;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import de.ellpeck.actuallyadditions.mod.items.ItemCoffee;
import de.ellpeck.actuallyadditions.mod.items.metalists.TheMiscItems;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityBase;
import de.ellpeck.actuallyadditions.mod.tile.TileEntityCoffeeMachine;
import de.ellpeck.actuallyadditions.mod.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class ContainerCoffeeMachine extends Container{

    private final TileEntityCoffeeMachine machine;

    public ContainerCoffeeMachine(InventoryPlayer inventory, TileEntityBase tile){
        this.machine = (TileEntityCoffeeMachine)tile;

        this.addSlotToContainer(new SlotItemHandlerUnconditioned(this.machine.slots, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS, 37, 6));
        this.addSlotToContainer(new SlotItemHandlerUnconditioned(this.machine.slots, TileEntityCoffeeMachine.SLOT_INPUT, 80, 42));
        this.addSlotToContainer(new SlotOutput(this.machine.slots, TileEntityCoffeeMachine.SLOT_OUTPUT, 80, 73));

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 2; j++){
                this.addSlotToContainer(new SlotItemHandlerUnconditioned(this.machine.slots, j+i*2+3, 125+j*18, 6+i*18));
            }
        }

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 9; j++){
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, 8+j*18, 97+i*18));
            }
        }
        for(int i = 0; i < 9; i++){
            this.addSlotToContainer(new Slot(inventory, i, 8+i*18, 155));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        int inventoryStart = 11;
        int inventoryEnd = inventoryStart+26;
        int hotbarStart = inventoryEnd+1;
        int hotbarEnd = hotbarStart+8;

        Slot theSlot = this.inventorySlots.get(slot);

        if(theSlot != null && theSlot.getHasStack()){
            ItemStack newStack = theSlot.getStack();
            ItemStack currentStack = newStack.copy();

            //Slots in Inventory to shift from
            if(slot == TileEntityCoffeeMachine.SLOT_OUTPUT){
                if(!this.mergeItemStack(newStack, inventoryStart, hotbarEnd+1, true)){
                    return StackUtil.getEmpty();
                }
                theSlot.onSlotChange(newStack, currentStack);
            }
            //Other Slots in Inventory excluded
            else if(slot >= inventoryStart){
                //Shift from Inventory
                if(newStack.getItem() == InitItems.itemMisc && newStack.getItemDamage() == TheMiscItems.CUP.ordinal()){
                    if(!this.mergeItemStack(newStack, TileEntityCoffeeMachine.SLOT_INPUT, TileEntityCoffeeMachine.SLOT_INPUT+1, false)){
                        return StackUtil.getEmpty();
                    }
                }
                else if(ItemCoffee.getIngredientFromStack(newStack) != null){
                    if(!this.mergeItemStack(newStack, 3, 11, false)){
                        return StackUtil.getEmpty();
                    }
                }
                else if(newStack.getItem() == InitItems.itemCoffeeBean){
                    if(!this.mergeItemStack(newStack, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS, TileEntityCoffeeMachine.SLOT_COFFEE_BEANS+1, false)){
                        return StackUtil.getEmpty();
                    }
                }
                //

                else if(slot >= inventoryStart && slot <= inventoryEnd){
                    if(!this.mergeItemStack(newStack, hotbarStart, hotbarEnd+1, false)){
                        return StackUtil.getEmpty();
                    }
                }
                else if(slot >= inventoryEnd+1 && slot < hotbarEnd+1 && !this.mergeItemStack(newStack, inventoryStart, inventoryEnd+1, false)){
                    return StackUtil.getEmpty();
                }
            }
            else if(!this.mergeItemStack(newStack, inventoryStart, hotbarEnd+1, false)){
                return StackUtil.getEmpty();
            }

            if(!StackUtil.isValid(newStack)){
                theSlot.putStack(StackUtil.getEmpty());
            }
            else{
                theSlot.onSlotChanged();
            }

            if(StackUtil.getStackSize(newStack) == StackUtil.getStackSize(currentStack)){
                return StackUtil.getEmpty();
            }
            theSlot.onTake(player, newStack);

            return currentStack;
        }
        return StackUtil.getEmpty();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player){
        return this.machine.canPlayerUse(player);
    }
}