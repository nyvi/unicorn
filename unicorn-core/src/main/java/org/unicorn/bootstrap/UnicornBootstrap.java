package org.unicorn.bootstrap;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.unicorn.core.DocumentScan;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动类
 *
 * @author czk
 */
@Component
public class UnicornBootstrap implements SmartLifecycle {

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private final DocumentScan documentScan;

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {
            documentScan.scan();
        }
    }

    @Override
    public void stop() {
        initialized.getAndSet(false);
    }

    @Override
    public boolean isRunning() {
        return initialized.get();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    public UnicornBootstrap(DocumentScan documentScan) {
        this.documentScan = documentScan;
    }
}
