package com.example.kybl.emailparser;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class RecursiveFinder extends RecursiveTask<LinkAnEmailHolder> {

    private static final int THRESHOLD = 50;

    private List<LinkAndEmailCallable> callables;

    public RecursiveFinder(List<LinkAndEmailCallable> callableList) {
        this.callables = callableList;
    }

    private LinkAnEmailHolder invokeAllCallables() throws InterruptedException, ExecutionException {
        LinkAnEmailHolder holder = new LinkAnEmailHolder();
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);

        List<Future<LinkAnEmailHolder>> futures = pool.invokeAll(callables);

        for (Future<LinkAnEmailHolder> future : futures) {
            holder.getLinks().addAll(future.get().getLinks());
            holder.getEmails().addAll(future.get().getEmails());
        }
        return holder;
    }

    private void addResults(LinkAnEmailHolder holder, List<RecursiveFinder> tasks) throws ExecutionException, InterruptedException {
        for (RecursiveFinder task : tasks) {
            holder.setLinks(task.get().getLinks());
            holder.setEmails(task.get().getEmails());
        }
    }

    @Override
    protected LinkAnEmailHolder compute() {
        if (callables.size() < THRESHOLD) {
            try {
                return invokeAllCallables();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            List<RecursiveFinder> tasks = new LinkedList<>();

            Set<String> links = Collections.synchronizedSet(new LinkedHashSet<String>());
            Set<String> emails = Collections.synchronizedSet(new LinkedHashSet<String>());

            LinkAnEmailHolder holder = new LinkAnEmailHolder(links, emails);

            List<LinkAndEmailCallable> task1 = callables.subList(0, callables.size() / 2);
            List<LinkAndEmailCallable> task2 = callables.subList(callables.size() / 2, callables.size());

            RecursiveFinder f1 = new RecursiveFinder(task1);
            RecursiveFinder f2 = new RecursiveFinder(task2);

            f1.fork();
            f2.fork();

            tasks.addAll(Arrays.asList(f1, f2));
            try {
                addResults(holder, tasks);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return holder;
        }
    }
}
