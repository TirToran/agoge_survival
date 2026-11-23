package net.torantyr.groundwork_sling;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {
    private final Screen parent;
    private Button soundButton;

    public ModConfigScreen(Screen parent) {
        super(Component.literal("Groundwork: Sling Settings"));
        this.parent = parent;
    }

    private Component soundLabel() {
        return Component.literal("Sling sounds: " + (ModClientConfig.SLING_SOUNDS.get() ? "ON" : "OFF"));
    }

    @Override
    protected void init() {
        soundButton = Button.builder(soundLabel(), button -> {
            ModClientConfig.SLING_SOUNDS.set(!ModClientConfig.SLING_SOUNDS.get());
            ModClientConfig.SLING_SOUNDS.save(); // writes the change to the .toml file
            button.setMessage(soundLabel());
        }).bounds(this.width / 2 - 100, this.height / 2 - 10, 200, 20).build();
        this.addRenderableWidget(soundButton);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height / 2 + 20, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent); // go back to the mod list
    }
}