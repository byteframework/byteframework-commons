package com.byteframework.commons.executor;

import java.util.concurrent.*;

/**
 * 线程池
 */
public class Executor {

    private static ExecutorService es = null;

    static {
        int minThreadNum = 5;
        int maxThreadNum = Runtime.getRuntime().availableProcessors();
        if (maxThreadNum < minThreadNum) {
            minThreadNum = maxThreadNum;
        }
        es = new ThreadPoolExecutor(minThreadNum, maxThreadNum, 60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public static void submit(Executor.QueryTask task) {
        es.submit(task);
    }

    public static void submit(Runnable task) {
        es.submit(task);
    }


    public static interface QueryHandler {
        void doQuery();
    }

    public static class QueryTask implements Runnable {
        private Executor.QueryHandler queryHandler;
        private CountDownLatch cdl;

        public QueryTask(CountDownLatch cdl, QueryHandler queryHandler) {
            this.cdl = cdl;
            this.queryHandler = queryHandler;
        }

        @Override
        public void run() {
            try {
                this.queryHandler.doQuery();
            } catch (Exception ex) {

            }
            this.cdl.countDown();
        }
    }
}



