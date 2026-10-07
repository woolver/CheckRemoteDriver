package ru.dmiaa.core;

import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfiguration;
import org.junit.platform.engine.support.hierarchical.ParallelExecutionConfigurationStrategy;

public class CustomSeleniumStrategy implements ParallelExecutionConfiguration, ParallelExecutionConfigurationStrategy {
    int parallelCount = Integer.parseInt(System.getProperty("parallel.core.max"));

    @Override
    public int getParallelism() { return parallelCount; }

    @Override
    public int getMinimumRunnable() {
        return 0;
    }

    @Override
    public int getMaxPoolSize() {
        return parallelCount;
    }

    @Override
    public int getCorePoolSize() {
        return parallelCount;
    }

    @Override
    public int getKeepAliveSeconds() {
        return 30;
    }

    @Override
    public ParallelExecutionConfiguration createConfiguration(final ConfigurationParameters configurationParameters) {
        return this;
    }
}
