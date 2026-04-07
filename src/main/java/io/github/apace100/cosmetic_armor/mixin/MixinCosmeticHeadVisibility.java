package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(value = LivingEntityRenderer.class)
public class MixinCosmeticHeadVisibility<T extends Mob, S extends HumanoidRenderState, M extends HumanoidModel<S>> {

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack modifyVisible(LivingEntity entity, EquipmentSlot slot) {
        ItemStack equippedStack = entity.getItemBySlot(slot);
        ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
        if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.is(CosmeticArmor.ALWAYS_VISIBLE))) {
            return cosmeticStack;
        }
        return equippedStack;
    }
}