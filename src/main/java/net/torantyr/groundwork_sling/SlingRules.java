package net.torantyr.groundwork_sling;

public class SlingRules {
    // set on a client by the server; empty means "use my own config"
    private static volatile Boolean syncedFireCharges = null;

    public static boolean fireChargesAllowed() {
        Boolean synced = syncedFireCharges;
        return synced != null ? synced : ModCommonConfig.FIRE_CHARGES_ALLOWED.get();
    }

    public static void setSyncedFireCharges(Boolean value) {
        syncedFireCharges = value;
    }
}