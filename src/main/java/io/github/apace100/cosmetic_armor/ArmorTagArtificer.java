package io.github.apace100.cosmetic_armor;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.ArtificeResource;
import com.swordglowsblue.artifice.impl.ArtificeDataResourcePackProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorTagArtificer {

    public static void artifice() {
        HashMap<String, EquipmentSlot> slots = new HashMap<>();
        slots.put("cosmetic_boots", EquipmentSlot.FEET);
        slots.put("cosmetic_leggings", EquipmentSlot.LEGS);
        slots.put("cosmetic_chestplate", EquipmentSlot.CHEST);
        slots.put("cosmetic_helmet", EquipmentSlot.HEAD);
        Artifice.registerDataPack(new Identifier(CosmeticArmor.MODID, "automatic_tags"), pack -> {
            for (Map.Entry<String, EquipmentSlot> slotEntry: slots.entrySet()) {
                pack.addItemTag(new Identifier("curios", slotEntry.getKey()), tag -> {
                    EquipmentSlot slot = slotEntry.getValue();
                    for (Item item: Registry.ITEM) {
                        if(MobEntity.getPreferredEquipmentSlot(new ItemStack(item)) == slot) {
                            tag.value(Registry.ITEM.getId(item));
                        }
                    }
                });
            }
        });
    }
}
