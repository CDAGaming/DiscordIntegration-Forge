package de.erdbeerbaerlp.dcintegration.forge.mixin;

import com.mojang.authlib.GameProfile;
import de.erdbeerbaerlp.dcintegration.common.storage.Configuration;
import de.erdbeerbaerlp.dcintegration.common.storage.PlayerLinkController;
import de.erdbeerbaerlp.dcintegration.common.util.Variables;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

@Mixin(PlayerList.class)
public class MixinWhitelist {
    @Inject(method = "canPlayerLogin", at = @At("HEAD"), cancellable = true)
    private void canLogin(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Component> cir) {
        if (Configuration.instance().linking.whitelistMode && ServerLifecycleHooks.getCurrentServer().usesAuthentication()) {
            try {
                if (!PlayerLinkController.isPlayerLinked(profile.getId())) {
                    cir.setReturnValue(new TextComponent(Configuration.instance().localization.linking.notWhitelistedCode.replace("%code%",""+Variables.discord_instance.genLinkNumber(profile.getId()))));
                }
            } catch (IllegalStateException e) {
                cir.setReturnValue(new TextComponent("Please check " + Variables.discordDataDir + "LinkedPlayers.json\n\n" + e.toString()));
            }
        }
    }
}
