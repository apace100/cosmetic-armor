package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = LivingEntityRenderer.class)
public class MixinCosmeticHeadVisibility<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>> {

    @Redirect(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack modifyVisible(LivingEntity entity, EquipmentSlot slot) {
        ItemStack equippedStack = entity.getEquippedStack(slot);
        ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
        if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.isIn(CosmeticArmor.ALWAYS_VISIBLE))) {
            return cosmeticStack;
        }
        return equippedStack;
    }
}