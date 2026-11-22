package net.torantyr.agoge_survival.items.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.torantyr.agoge_survival.ModTags;
import net.torantyr.agoge_survival.entity.custom.SlingProjectile;
import net.torantyr.agoge_survival.items.ModItems;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SlingItem extends BowItem {

    public SlingItem(Properties pProperties) {
        super(pProperties);
    }

    private static final int FULL_DRAW_TICKS = 40; // 20 ticks = 1 second
    public static final int CRIT_TICKS = 60;       // crit threshold, also when the max loop starts

    // What counts as ammo: anything in the sling_ammo tag
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModTags.Items.SLING_AMMO);
    }

    private static float getSlingPower(int ticksHeld) {
        return Math.min((float) ticksHeld / FULL_DRAW_TICKS, 1.0F);
    }

    // Runs when the player lets go of right-click
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) {
            return;
        }

        // Find ammo in the inventory (in creative this returns a stand-in, so no ammo is needed)
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) {
            return;
        }

        // How long was right-click held? Ticks held becomes a power from 0.0 to 1.0
        int ticksHeld = this.getUseDuration(stack) - timeLeft;
        float power = getSlingPower(ticksHeld);
        if (power < 0.1F) {
            return; // too short a pull, do nothing
        }

        if (!level.isClientSide) {
            SlingProjectile projectile = new SlingProjectile(level, player);
            float base = baseDamageFor(ammo);
            int powerLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
            if (powerLevel > 0) {
                base *= 1.0F + 0.25F * (powerLevel + 1); // the bow's boost: +50% at Power I up to +150% at Power V
            }
            projectile.setBaseDamage(base);
            projectile.setKnockback(EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack));
            if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                projectile.setFlame(true);
            }
            projectile.setCrit(ticksHeld >= CRIT_TICKS);
            projectile.setItem(ammo.is(ModTags.Items.SLING_AMMO)
                    ? new ItemStack(ammo.getItem())
                    : new ItemStack(Items.FLINT));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
            level.addFreshEntity(projectile);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
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

    private static float baseDamageFor(ItemStack ammo) {
        if (ammo.is(ModItems.NETHERITE_BULLET.get())) return 2.5F;
        if (ammo.is(Items.DIAMOND)) return 2.0F;
        if (ammo.is(ModTags.Items.STEEL_NUGGETS) || ammo.is(Items.IRON_NUGGET)) return 1.6F;
        if (ammo.is(ModTags.Items.COPPER_NUGGETS) || ammo.is(ModTags.Items.TIN_NUGGETS)
                || ammo.is(Items.GOLD_NUGGET)) return 1.33F;
        return 1.0F; // flint, and anything else
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // stops the bow's built-in pull-back pose
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SlingClientExtensions());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide && entity instanceof Player player) {
            int ticksHeld = this.getUseDuration(stack) - remainingUseDuration;
            SlingClientSounds.onUseTick(player, ticksHeld);
        }
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.INFINITY_ARROWS
                && super.canApplyAtEnchantingTable(stack, enchantment);
    }
}