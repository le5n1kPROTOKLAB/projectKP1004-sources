package com.example.carinfoapp.data;

public class StateManager {
    private static final class InstanceHolder {
        static final StateManager instance = new StateManager();
    }

    public static StateManager getInstance() {
        return InstanceHolder.instance;
    }

    public final DistributionBoxManager distributionBoxManager;
    private StateManager() {
        distributionBoxManager = new DistributionBoxManager();
    }
}
