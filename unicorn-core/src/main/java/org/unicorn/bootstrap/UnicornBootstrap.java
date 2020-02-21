package org.unicorn.bootstrap;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.unicorn.core.DocumentScanService;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动类
 *
 * @author czk
 */
@Component
public class UnicornBootstrap implements SmartLifecycle {

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private final DocumentScanService documentScanService;

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {
            documentScanService.scan();
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

    public UnicornBootstrap(DocumentScanService documentScanService) {
        this.documentScanService = documentScanService;
    }
}
