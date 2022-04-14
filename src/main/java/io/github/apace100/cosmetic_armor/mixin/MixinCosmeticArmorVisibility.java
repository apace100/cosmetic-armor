package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public class MixinCosmeticArmorVisibility {

	@Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
	private ItemStack modifyVisibleArmor(LivingEntity entity, EquipmentSlot slot) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !equippedStack.isIn(CosmeticArmor.ALWAYS_VISIBLE))) {
			return cosmeticStack;
		}
		return equippedStack;
	}
}
