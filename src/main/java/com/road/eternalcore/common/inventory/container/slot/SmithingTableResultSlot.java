package com.road.eternalcore.common.inventory.container.slot;

import com.road.eternalcore.common.inventory.SmithingTableInventory;
import com.road.eternalcore.common.item.crafting.IModRecipeType;
import com.road.eternalcore.common.item.tool.CustomTierItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.function.Consumer;

public class SmithingTableResultSlot extends Slot {
    // 笔记：CraftingResultSlot里的removeCount是用来记录统计数据的，不用抄
    protected final SmithingTableInventory inputSlots;
    protected final PlayerEntity player;

    public SmithingTableResultSlot(PlayerEntity player, SmithingTableInventory inputSlots, IInventory resultSlots, int index, int x, int y) {
        super(resultSlots, index, x, y);
        this.player = player;
        this.inputSlots = inputSlots;
    }

    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    protected void checkTakeAchievements(ItemStack itemStack) {
        // 没有onCraftBy,那个基本上就是用来加统计数据的
        if (this.container instanceof IRecipeHolder) {
            ((IRecipeHolder)this.container).awardUsedRecipes(this.player);
        }
    }

    public ItemStack onTake(PlayerEntity player, ItemStack itemStack) {
        checkTakeAchievements(itemStack);
        ForgeHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remainItemList = player.level.getRecipeManager().getRemainingItemsFor(IModRecipeType.SMITHING, this.inputSlots, player.level);
        ForgeHooks.setCraftingPlayer(null);
        // 消耗材料，减少工具耐久
        Consumer<Integer> normalRemainingItem = i -> {
            ItemStack originalItem = inputSlots.getItem(i);
            ItemStack remainItem = remainItemList.get(i);
            if (!originalItem.isEmpty()) {
                inputSlots.removeItem(i, 1);
                originalItem = inputSlots.getItem(i);
            }
            if (!remainItem.isEmpty()) {
                if (originalItem.isEmpty()) {
                    inputSlots.setItem(i, remainItem);
                } else if (ItemStack.isSame(originalItem, remainItem) && ItemStack.tagMatches(originalItem, remainItem)) {
                    remainItem.grow(originalItem.getCount());
                    inputSlots.setItem(i, remainItem);
                } else if (!this.player.inventory.add(remainItem)) {
                    this.player.drop(remainItem, false);
                }
            }
        };
        for(int i = 1; i < inputSlots.getContainerSize(); i++){
            normalRemainingItem.accept(i);
        }
        ItemStack toolItem = inputSlots.getItem(0);
        if (toolItem.isDamageableItem()){
            ItemStack usedTool = remainItemList.get(0);
            usedTool.hurtAndBreak(
                    CustomTierItem.addItemDamage(toolItem, 0),
                    player,
                    (playerEntity) -> ForgeEventFactory.onPlayerDestroyItem(playerEntity, toolItem, null)
            );
            inputSlots.setItem(0, usedTool);
        }else{
            normalRemainingItem.accept(0);
        }
        return itemStack;
    }
}
