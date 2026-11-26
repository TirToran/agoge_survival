package net.torantyr.groundwork_sling.items.custom;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class SlingRenderer extends GeoItemRenderer<SlingItem> {
    public SlingRenderer() {
        super(new SlingModel());
    }
}