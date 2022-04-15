package io.github.apace100.cosmetic_armor.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = ArmorFeatureRenderer.class, priority = 800)
public abstract class MixinCosmeticArmorVisibility<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

	@Shadow protected abstract void setVisible(A bipedModel, EquipmentSlot slot);

	@Shadow protected abstract boolean usesSecondLayer(EquipmentSlot slot);

	@Shadow protected abstract void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, @Nullable String overlay);

	public MixinCosmeticArmorVisibility(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void renderCustomArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot slot, int light, A model, CallbackInfo ci) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !CosmeticArmor.ALWAYS_VISIBLE.contains(equippedStack.getItem()))) {
			ArmorRenderer renderer = ArmorRendererRegistryImpl.get(cosmeticStack.getItem());

			if (renderer != null) {
				renderer.render(matrices, vertexConsumers, cosmeticStack, entity, slot, light,
					(BipedEntityModel<LivingEntity>) getContextModel());
				ci.cancel();
			} else {
				if(ArmorRendererRegistryImpl.get(equippedStack.getItem()) != null) {
					cosmeticarmor$renderArmor(matrices, vertexConsumers, cosmeticStack, slot, light, model);
					ci.cancel();
				}
			}
		}
	}

	@Redirect(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
	private ItemStack modifyVisibleArmor(LivingEntity entity, EquipmentSlot slot) {
		ItemStack equippedStack = entity.getEquippedStack(slot);
		ItemStack cosmeticStack = CosmeticArmor.getCosmeticArmor(entity, slot);
		if(!cosmeticStack.isEmpty() && (equippedStack.isEmpty() || !CosmeticArmor.ALWAYS_VISIBLE.contains(equippedStack.getItem()))) {
			return cosmeticStack;
		}
		return equippedStack;
	}

	@Unique
	private void cosmeticarmor$renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack itemStack, EquipmentSlot armorSlot, int light, A model) {
		if (!(itemStack.getItem() instanceof ArmorItem)) {
			return;
		}
		ArmorItem armorItem = (ArmorItem)itemStack.getItem();
		if (armorItem.getSlotType() != armorSlot) {
			return;
		}
		this.getContextModel().setAttributes(model);
		this.setVisible(model, armorSlot);
		boolean bl = this.usesSecondLayer(armorSlot);
		boolean bl2 = itemStack.hasGlint();
		if (armorItem instanceof DyeableArmorItem) {
			int i = ((DyeableArmorItem)armorItem).getColor(itemStack);
			float f = (float)(i >> 16 & 0xFF) / 255.0f;
			float g = (float)(i >> 8 & 0xFF) / 255.0f;
			float h = (float)(i & 0xFF) / 255.0f;
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, f, g, h, null);
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0f, 1.0f, 1.0f, "overlay");
		} else {
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, bl2, model, bl, 1.0f, 1.0f, 1.0f, null);
		}
	}
}
