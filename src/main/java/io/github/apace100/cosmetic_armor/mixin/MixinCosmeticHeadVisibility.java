package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeadFeatureRenderer.class)
public class MixinCosmeticHeadVisibility {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack modifyVisibleHead(LivingEntity livingEntity, EquipmentSlot slot) {
        ItemStack cosmetic = CosmeticArmor.getStackInCosmeticSlot(livingEntity, slot);
        if(!cosmetic.isEmpty()) {
            return cosmetic;
        }
        return livingEntity.getEquippedStack(slot);
    }
}
