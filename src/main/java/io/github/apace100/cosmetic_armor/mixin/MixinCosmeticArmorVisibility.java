package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = HumanoidMobRenderer.class)
public class MixinCosmeticArmorVisibility<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> {

    @Redirect(method = "extractHumanoidRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/HumanoidMobRenderer;getEquipmentIfRenderable(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack modifyVisible(LivingEntity entity, EquipmentSlot slot) {
        ItemStack equippedStack = entity.getItemBySlot(slot);
        ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
        if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.is(CosmeticArmor.ALWAYS_VISIBLE))) {
            return cosmeticStack;
        }
        return equippedStack;
    }
}