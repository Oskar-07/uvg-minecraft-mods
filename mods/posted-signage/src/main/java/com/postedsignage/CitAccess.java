package com.postedsignage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** One server-side policy shared by door labels and the existing CIT signs. */
public final class CitAccess {
    private CitAccess() {}
    public static boolean canEdit(Player player) {
        return player.isCreative() && player.hasPermissions(2);
    }
    public static boolean canEditAt(Player player, BlockPos pos) {
        return canEdit(player) && player.distanceToSqr(pos.getCenter()) <= 64
                && player.level().mayInteract(player, pos);
    }
}
