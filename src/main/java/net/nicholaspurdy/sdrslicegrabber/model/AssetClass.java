package net.nicholaspurdy.sdrslicegrabber.model;

public enum AssetClass {

    COMMODITIES, CREDITS, EQUITIES, FOREX, RATES;

    public static AssetClass convertAbbrv(String a) {

        if ("CO".equals(a)) return COMMODITIES;
        if ("CR".equals(a)) return CREDITS;
        if ("EQ".equals(a)) return EQUITIES;
        if ("FX".equals(a)) return FOREX;
        if ("IR".equals(a)) return RATES;

        throw new IllegalArgumentException(a + " is not a valid asset class.");
    }

}
