package net.torantyr.groundwork_sling;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class VolumeSlider extends AbstractSliderButton {

    public VolumeSlider(int x, int y, int width, int height, double initialValue) {
        super(x, y, width, height, Component.empty(), initialValue);
        this.updateMessage(); // set the label text right away
    }

    // the text on the slider
    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal("Sling sound volume: " + Math.round(this.value * 100) + "%"));
    }

    // runs when the player drags the slider; the file is saved when the screen closes
    @Override
    protected void applyValue() {
        ModClientConfig.SOUND_VOLUME.set(this.value);
    }
}