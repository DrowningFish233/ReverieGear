package com.drowningfish233.reveriegear.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class RFDataGenerators {
    private RFDataGenerators() {
    }

    public static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(event.includeClient(), new RFLanguageProvider(event.getGenerator().getPackOutput(), "en_us"));
        event.getGenerator().addProvider(event.includeClient(), new RFLanguageProvider(event.getGenerator().getPackOutput(), "zh_cn"));
    }
}
