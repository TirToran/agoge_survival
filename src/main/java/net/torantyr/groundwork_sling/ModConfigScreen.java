package net.torantyr.groundwork_sling;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.torantyr.groundwork_sling.network.ModNetwork;
import net.torantyr.groundwork_sling.network.SetFireChargesPacket;

public class ModConfigScreen extends Screen {
    private final Screen parent;
    private Button soundButton;
    private Button fireButton; // shared by init(), fireLabel() and tick(), so it has to be a field

    public ModConfigScreen(Screen parent) {
        super(Component.literal("Groundwork Sling Settings"));
        this.parent = parent;
    }

    private Component soundLabel() {
        return Component.literal("Sling sounds: " + (ModClientConfig.SLING_SOUNDS.get() ? "ON" : "OFF"));
    }

    private Component fireLabel() {
        return Component.literal("Fire charges as ammo: " + (SlingRules.fireChargesAllowed() ? "ON" : "OFF")
                + (fireButton != null && !fireButton.active ? " (admin only)" : ""));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // sound on/off toggle (a personal setting)
        soundButton = Button.builder(soundLabel(), button -> {
            ModClientConfig.SLING_SOUNDS.set(!ModClientConfig.SLING_SOUNDS.get());
            button.setMessage(soundLabel());
        }).bounds(centerX - 100, centerY - 34, 200, 20).build();
        this.addRenderableWidget(soundButton);

        // volume slider (a personal setting)
        this.addRenderableWidget(new VolumeSlider(centerX - 100, centerY - 10, 200, 20,
                ModClientConfig.SOUND_VOLUME.get()));

        // fire charge toggle (a server rule: admins only)
        Minecraft mc = Minecraft.getInstance();
        boolean admin = mc.player != null && mc.player.hasPermissions(2);
        fireButton = Button.builder(fireLabel(), button ->
                ModNetwork.CHANNEL.sendToServer(new SetFireChargesPacket(!SlingRules.fireChargesAllowed()))
        ).bounds(centerX - 100, centerY + 14, 200, 20).build();
        fireButton.active = admin; // greyed out for non-admins and from the main menu
        fireButton.setMessage(fireLabel()); // refresh the label now that we know if it's active
        this.addRenderableWidget(fireButton);

        // done button
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(centerX - 100, centerY + 44, 200, 20).build());
    }

    // keeps the fire charge label in step when the server's answer arrives
    @Override
    public void tick() {
        if (fireButton != null) {
            fireButton.setMessage(fireLabel());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        ModClientConfig.SLING_SOUNDS.save();  // write the personal settings to the toml file
        ModClientConfig.SOUND_VOLUME.save();
        this.minecraft.setScreen(parent);
    }
}