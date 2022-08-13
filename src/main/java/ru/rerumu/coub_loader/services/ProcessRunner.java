package ru.rerumu.coub_loader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rerumu.coub_loader.services.helper.StdProcessorCallable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProcessRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<String> args;

    public ProcessRunner(List<String> args){
        this.args = args;
    }

    public void run()throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder pb = new ProcessBuilder(args);
        Process process = pb.start();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
        BufferedInputStream bufferedErrorStream = new BufferedInputStream(process.getErrorStream());
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Integer>> futureList =new ArrayList<>();
        futureList.add(executorService.submit(new StdProcessorCallable(bufferedInputStream,logger::debug)));
        futureList.add(executorService.submit(new StdProcessorCallable(bufferedErrorStream,logger::error)));

        logger.info("Closing process");
        int exitCode = process.waitFor();
        executorService.shutdown();
        int threadRes=0;
        for (Future<Integer> future: futureList){
            Integer tmp = future.get();
            if (tmp!=0){
                threadRes=tmp;
            }
        }
        if (exitCode != 0 || threadRes != 0) {
            logger.error("Process or thread closed with error");
            throw new IOException();
        }
        logger.info("Process closed");
    }
}
