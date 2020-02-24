package org.unicorn.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;
import org.unicorn.core.DocumentScanService;
import org.unicorn.util.ReflectionUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动类
 *
 * @author czk
 */
@Slf4j
@Component
public class UnicornBootstrap implements SmartLifecycle {

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private final DocumentScanService documentScanService;

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {
            try {
                documentScanService.scan();
            } catch (Exception e) {
                log.error("出错啦!!!去https://github.com/nyvi/unicorn提Issues. 错误信息:", e);
            } finally {
                ReflectionUtils.clear();
            }
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
