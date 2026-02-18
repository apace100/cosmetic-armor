package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
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
@Mixin(value = BipedEntityRenderer.class)
public class MixinCosmeticArmorVisibility<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>> {

	@Redirect(method = "updateBipedRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/BipedEntityRenderer;getEquippedStack(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
	private static ItemStack modifyVisible(LivingEntity entity, EquipmentSlot slot) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.isIn(CosmeticArmor.ALWAYS_VISIBLE))) {
			return cosmeticStack;
		}
		return equippedStack;
	}
}