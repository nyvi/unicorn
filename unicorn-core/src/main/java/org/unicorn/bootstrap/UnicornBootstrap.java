package org.unicorn.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.unicorn.core.DocumentCache;
import org.unicorn.core.DocumentService;
import org.unicorn.util.ReflectionUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动类
 *
 * @author czk
 */
@Component

public class UnicornBootstrap implements SmartLifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(UnicornBootstrap.class);

    private AtomicBoolean initialized = new AtomicBoolean(false);

    private final Environment environment;

    private final DocumentService documentService;

    @Override
    public void start() {
        if (initialized.compareAndSet(false, true)) {
            try {
                documentService.mappingScan();
            } catch (Exception e) {
                LOG.error("出错啦!!!去https://github.com/nyvi/unicorn提Issues. 错误信息:", e);
            } finally {
                ReflectionUtils.clear();
            }
        }
    }

    @Override
    public boolean isAutoStartup() {
        String autoStartupConfig = environment.getProperty("spring.unicorn.auto-startup", "true");
        return Boolean.parseBoolean(autoStartupConfig);
    }

    @Override
    public void stop() {
        initialized.getAndSet(false);
        DocumentCache.clear();
    }

    @Override
    public boolean isRunning() {
        return initialized.get();
    }

    public UnicornBootstrap(Environment environment, DocumentService documentService) {
        this.environment = environment;
        this.documentService = documentService;
    }

}
