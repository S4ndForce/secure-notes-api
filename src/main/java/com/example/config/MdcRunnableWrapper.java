package com.example.config;

import org.slf4j.MDC;
import java.util.Map;

public class MdcRunnableWrapper implements Runnable {

    private final Runnable delegate;
    private final Map<String, String> context;

    public MdcRunnableWrapper(Runnable delegate) {
        this.delegate = delegate;
        this.context = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        if (context != null) {
            MDC.setContextMap(context);
        }
        try {
            delegate.run();
        } finally {
            MDC.clear();
        }
    }
}
