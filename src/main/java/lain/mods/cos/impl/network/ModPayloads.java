package lain.mods.cos.impl.network;

import lain.mods.cos.impl.InventoryManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.network.payload.*;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPayloads {

    public static void setupPayloads(PayloadRegistrar registrar) {
        registrar.playToClient(PayloadSyncCosArmor.TYPE, PayloadSyncCosArmor.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setStackInSlot(p.slot(), p.itemCosArmor());
                ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setSkinArmor(p.slot(), p.isSkinArmor());
            });
        }).playToServer(PayloadSetSkinArmor.TYPE, PayloadSetSkinArmor.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                ModObjects.invMan.getCosArmorInventory(c.player().getUUID()).setSkinArmor(p.slot(), p.isSkinArmor());
            });
        }).playToServer(PayloadOpenCosArmorInventory.TYPE, PayloadOpenCosArmorInventory.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                c.player().openMenu(ModObjects.invMan.getCosArmorInventory(c.player().getUUID()));
            });
        }).playToServer(PayloadOpenNormalInventory.TYPE, PayloadOpenNormalInventory.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                c.player().closeContainer();
            });
        }).playToClient(PayloadSyncHiddenFlags.TYPE, PayloadSyncHiddenFlags.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                if (InventoryManager.checkIdentifier(p.modid(), p.identifier())) {
                    ModObjects.invMan.getCosArmorInventoryClient(p.uuid()).setHidden(p.modid(), p.identifier(), p.hidden());
                }
            });
        }).playToServer(PayloadSetHiddenFlags.TYPE, PayloadSetHiddenFlags.STREAM_CODEC, (p, c) -> {
            c.enqueueWork(() -> {
                if (InventoryManager.checkIdentifier(p.modid(), p.identifier())) {
                    ModObjects.invMan.getCosArmorInventory(c.player().getUUID()).setHidden(p.modid(), p.identifier(), p.hidden());
                }
            });
        });
    }

}
