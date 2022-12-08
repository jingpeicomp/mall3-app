package com.bik.web3.mall3.common.configure.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 异常捕获线程池
 *
 * @author Mingo.Liu
 * @date 2022-12-08
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingTaskExecutor implements AsyncTaskExecutor, InitializingBean, DisposableBean {

    private final AsyncTaskExecutor executor;

    @Override
    public void destroy() throws Exception {
        if (executor instanceof DisposableBean) {
            DisposableBean bean = (DisposableBean) executor;
            bean.destroy();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor instanceof InitializingBean) {
            InitializingBean bean = (InitializingBean) executor;
            bean.afterPropertiesSet();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(@NonNull Runnable task, long startTimeout) {
        executor.execute(createWrappedRunnable(task), startTimeout);
    }

    @Override
    public void execute(@NonNull Runnable task) {
        executor.execute(task);
    }

    @Override
    @NonNull
    public Future<?> submit(@NonNull Runnable task) {
        return executor.submit(createWrappedRunnable(task));
    }

    @Override
    @NonNull
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return executor.submit(createCallable(task));
    }

    protected void handle(Exception e) {
        log.error("Caught async exception", e);
    }

    private <T> Callable<T> createCallable(final Callable<T> task) {
        return () -> {
            {
                try {
                    return task.call();
                } catch (Exception e) {
                    handle(e);
                    throw e;
                }
            }
        };
    }

    private Runnable createWrappedRunnable(final Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (Exception e) {
                handle(e);
            }
        };
    }
}
