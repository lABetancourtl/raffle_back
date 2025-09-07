package com.aba.raffle.proyecto.entities;

public enum EmailColor {
    SUNNY_YELLOW("#FFFF00"),
    OCEAN_BLUE("#00BCD4"),
    MINT_GREEN("#4CAF50"),
    CORAL_ORANGE("#FF5722"),
    PURPLE_RAIN("#9C27B0");

    private final String hexCode;

    EmailColor(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getHexCode() {
        return hexCode;
    }
}

