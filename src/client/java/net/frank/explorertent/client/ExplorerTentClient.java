package net.frank.explorertent.client;

import net.fabricmc.api.ClientModInitializer;

public class ExplorerTentClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TentClient.register();
    }
}
