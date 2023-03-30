package com.road.eternalcore.common.tileentity;

import com.road.eternalcore.Utils;
import com.road.eternalcore.common.block.machine.MachineBlocks;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModTileEntityRegistries {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Utils.MOD_ID);
    public static final RegistryObject<TileEntityType<LockerTileEntity>> locker = register(
            "locker",
            LockerTileEntity::new,
            MachineBlocks.locker
    );

    public static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> supplier, RegistryObject<Block> block){
        return TILE_ENTITY.register(name, () -> TileEntityType.Builder.of(supplier, block.get()).build(null));
    }
    public static void register(IEventBus bus) {
        TILE_ENTITY.register(bus);
    }
}
