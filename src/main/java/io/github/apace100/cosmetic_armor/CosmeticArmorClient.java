package io.github.apace100.cosmetic_armor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class CosmeticArmorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
            .register(((spriteAtlasTexture, registry) -> {
                registry.register(new Identifier("cosmetic-armor:gui/cosmetic_boots_icon"));
                registry.register(new Identifier("cosmetic-armor:gui/cosmetic_chestplate_icon"));
                registry.register(new Identifier("cosmetic-armor:gui/cosmetic_helmet_icon"));
                registry.register(new Identifier("cosmetic-armor:gui/cosmetic_leggings_icon"));
            }));
    }
}
