package net.torantyr.groundwork_sling.items.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.torantyr.groundwork_sling.ModTags;
import net.torantyr.groundwork_sling.entity.custom.SlingProjectile;
import net.torantyr.groundwork_sling.items.ModItems;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SlingItem extends BowItem {

    // =====================================================================
    // CONSTANTS: timing for the draw. 20 ticks = 1 second. ***
    // =====================================================================
    private static final int FULL_DRAW_TICKS = 40; // ticks held to reach full power
    public static final int CRIT_TICKS = 60;       // ticks held to earn a crit; also when the max loop sound starts


    // =====================================================================
    // CONSTRUCTOR: the game builds the item through this.
    // =====================================================================
    public SlingItem(Properties pProperties) {
        super(pProperties);
    }


    // =====================================================================
    // AMMO: what the sling accepts and how hard each kind hits.
    // =====================================================================

    // What counts as ammo: anything in the sling_ammo tag
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ModTags.Items.SLING_AMMO);
    }

    // Base damage per ammo type. Final damage = this x projectile speed, rounded up, like an arrow.
    private static float baseDamageFor(ItemStack ammo) {
        if (ammo.is(ModItems.NETHERITE_BULLET.get())) return 2.5F;
        if (ammo.is(Items.DIAMOND)) return 2.0F;
        if (ammo.is(Items.FIRE_CHARGE)) return 2.0F;   // also sets the target on fire (see releaseUsing)
        if (ammo.is(ModTags.Items.STEEL_NUGGETS) || ammo.is(Items.IRON_NUGGET)) return 1.6F;
        if (ammo.is(ModTags.Items.COPPER_NUGGETS) || ammo.is(ModTags.Items.TIN_NUGGETS)
                || ammo.is(Items.GOLD_NUGGET)) return 1.33F;
        return 1.0F; // flint, and anything else (also the creative-mode stand-in)
    }


    // =====================================================================
    // DRAW POWER: how long you held right-click becomes a 0.0 to 1.0 power.
    // =====================================================================
    private static float getSlingPower(int ticksHeld) {
        return Math.min((float) ticksHeld / FULL_DRAW_TICKS, 1.0F);
    }


    // =====================================================================
    // FIRING: runs when the player lets go of right-click.
    // =====================================================================
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        // 1. Only players can fire the sling
        if (!(entity instanceof Player player)) {
            return;
        }

        // 2. Find ammo in the inventory (in creative this returns a stand-in, so no ammo is needed)
        ItemStack ammo = player.getProjectile(stack);
        if (ammo.isEmpty()) {
            return;
        }

        // 3. Work out the draw power; a tap that's too short does nothing
        int ticksHeld = this.getUseDuration(stack) - timeLeft;
        float power = getSlingPower(ticksHeld);
        if (power < 0.1F) {
            return;
        }

        // 4. Spawn the projectile (the server creates entities, never the client)
        if (!level.isClientSide) {
            if (ammo.is(Items.ENDER_PEARL)) {
                // a normal pearl (vanilla teleport), launched with the sling's draw power
                ThrownEnderpearl pearl = new ThrownEnderpearl(level, player);
                pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
                level.addFreshEntity(pearl);
            } else if (ammo.is(Items.FIRE_CHARGE)) {
                // the dispenser's fireball, launched from the player's eyes in the look direction
                Vec3 look = player.getLookAngle();
                SmallFireball fireball = new SmallFireball(level, player, look.x, look.y, look.z);
                fireball.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
                fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 1.5F, 0.0F);
                level.addFreshEntity(fireball);
            } else {
                SlingProjectile projectile = new SlingProjectile(level, player);

                // damage: the ammo's base, boosted by Power the way a bow's Power works
                float base = baseDamageFor(ammo);
                int powerLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                if (powerLevel > 0) {
                    base *= 1.0F + 0.25F * (powerLevel + 1);
                }
                projectile.setBaseDamage(base);

                // Punch (knockback), and burning from a fire charge or the Flame enchantment
                projectile.setKnockback(EnchantmentHelper.getTagEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack));

                projectile.setCrit(ticksHeld >= CRIT_TICKS);
                projectile.setItem(ammo.is(ModTags.Items.SLING_AMMO)
                        ? new ItemStack(ammo.getItem())
                        : new ItemStack(Items.FLINT));
                projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
                level.addFreshEntity(projectile);
            }

            // wear the sling down by one use (creative players are exempt)
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        // 5. Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 1.0F);

        // 6. Use up one piece of ammo, unless in creative
        if (!player.getAbilities().instabuild) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }
    }


    // =====================================================================
    // ENCHANTING: keep Infinity off the sling at the enchanting table.
    // (The anvil is handled by SlingAnvilEvents.)
    // =====================================================================
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment != Enchantments.INFINITY_ARROWS
                && super.canApplyAtEnchantingTable(stack, enchantment);
    }


    // =====================================================================
    // ANIMATION AND SOUND (client side): see SlingClientExtensions and SlingClientSounds.
    // =====================================================================

    // Turns off the bow's built-in pull-back pose, so our own animation is the only one
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    // Registers the first-person hand transform and the third-person arm pose
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SlingClientExtensions());
    }

    // Runs every tick while right-click is held; starts the wind-up and max sounds at the right moments
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide && entity instanceof Player player) {
            int ticksHeld = this.getUseDuration(stack) - remainingUseDuration;
            SlingClientSounds.onUseTick(player, ticksHeld);
        }
    }
}