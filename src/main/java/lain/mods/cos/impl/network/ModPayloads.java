package lain.mods.cos.impl.network;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.payload.*;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class ModPayloads {

    public static void setupPayloads(IPayloadRegistrar registrar) {
        registrar.play(PayloadSyncCosArmor.ID, PayloadSyncCosArmor::new, b -> {
            b.client((p, c) -> {
                c.workHandler().execute(() -> {
                    ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setStackInSlot(p.slot(), p.itemCosArmor());
                    ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setSkinArmor(p.slot(), p.isSkinArmor());
                });
            }).server(IPlayPayloadHandler.noop());
        }).play(PayloadSetSkinArmor.ID, PayloadSetSkinArmor::new, b -> {
            b.server((p, c) -> {
                c.workHandler().execute(() -> {
                    ModObjects.invMan.getCosArmorInventory(c.player().map(Entity::getUUID).get()).setSkinArmor(p.slot(), p.isSkinArmor());
                });
            }).client(IPlayPayloadHandler.noop());
        }).play(PayloadOpenCosArmorInventory.ID, PayloadOpenCosArmorInventory::new, b -> {
            b.server((p, c) -> {
                c.workHandler().execute(() -> {
                    c.player().get().openMenu(ModObjects.invMan.getCosArmorInventory(c.player().map(Entity::getUUID).get()));
                });
            }).client(IPlayPayloadHandler.noop());
        }).play(PayloadOpenNormalInventory.ID, PayloadOpenNormalInventory::new, b -> {
            b.server((p, c) -> {
                c.workHandler().execute(() -> {
                    c.player().get().closeContainer();
                });
            }).client(IPlayPayloadHandler.noop());
        }).play(PayloadSyncHiddenFlags.ID, PayloadSyncHiddenFlags::new, b -> {
            b.client((p, c) -> {
                c.workHandler().execute(() -> {
                    if (InventoryManager.checkIdentifier(p.modid(), p.identifier())) {
                        ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setHidden(p.modid(), p.identifier(), p.hidden());
                    }
                });
            }).server(IPlayPayloadHandler.noop());
        }).play(PayloadSetHiddenFlags.ID, PayloadSetHiddenFlags::new, b -> {
            b.server((p, c) -> {
                c.workHandler().execute(() -> {
                    if (InventoryManager.checkIdentifier(p.modid(), p.identifier())) {
                        ModObjects.invMan.getCosArmorInventory(c.player().map(Entity::getUUID).get()).setHidden(p.modid(), p.identifier(), p.hidden());
                    }
                });
            }).client(IPlayPayloadHandler.noop());
        });
    }

}
