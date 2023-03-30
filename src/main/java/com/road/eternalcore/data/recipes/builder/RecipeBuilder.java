package com.road.eternalcore.data.recipes.builder;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Consumer;

public class RecipeBuilder {
    protected final Item result;
    protected final CompoundNBT nbt = new CompoundNBT();
    protected final int count;
    protected String group;
    protected final Advancement.Builder advancement = Advancement.Builder.advancement();
    public RecipeBuilder(IItemProvider item, int count) {
        this.result = item.asItem();
        this.count = count;
    }

    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation key) {
    }

    public static class Result {

        protected final ResourceLocation id;
        protected final Item result;
        protected final CompoundNBT nbt;
        protected final int count;
        protected final String group;
        protected final Advancement.Builder advancement;
        protected final ResourceLocation advancementId;
        public Result(ResourceLocation id, Item result, CompoundNBT nbt, int count, String group, Advancement.Builder advancement, ResourceLocation advancementId) {
            this.id = id;
            this.result = result;
            this.nbt = nbt;
            this.count = count;
            this.group = group;
            this.advancement = advancement;
            this.advancementId = advancementId;
        }
        protected void serializeRecipeResult(JsonObject json){
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
            if (!this.nbt.isEmpty()) {
                resultJson.addProperty("nbt", this.nbt.toString());
            }
            if (this.count > 1) {
                resultJson.addProperty("count", this.count);
            }
            json.add("result", resultJson);
        }
    }
}
