package net.torantyr.agoge_survival.items.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.torantyr.agoge_survival.entity.custom.SlingProjectile;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SlingItem extends BowItem {

    public SlingItem(Properties pProperties) {
        super(pProperties);
    }

    // What counts as ammo
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(Items.FLINT);
    }

    // Runs when the player lets go of right-click
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }

        // Find ammo in the inventory (in creative this returns a stand-in, so no flint is needed)
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) {
            return;
        }

        // How long was right-click held? Ticks held becomes a power from 0.0 to 1.0
        int ticksHeld = this.getUseDuration(stack) - timeLeft;
        float power = getPowerForTime(ticksHeld);
        if (power < 0.1F) {
            return; // too short a pull, do nothing
        }

        if (!level.isClientSide) {
            SlingProjectile projectile = new SlingProjectile(level, player);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            level.addFreshEntity(projectile);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);

        // Use up one piece of ammo, unless in creative
        if (!player.getAbilities().instabuild) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // stops the bow's built-in pull-back pose
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SlingClientExtensions());
    }
}
