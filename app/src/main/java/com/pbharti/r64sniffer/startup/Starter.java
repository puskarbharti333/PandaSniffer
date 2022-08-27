package com.pbharti.r64sniffer.startup;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.utils.Log;

import java.util.HashSet;
import java.util.Set;



public class Starter {
    private static final String TAG = Constants.TAG + ".Starter";
    static boolean sFirstLaunch = true;
    private static Starter sInstance = null;
    private final Set<Task> mTasks = new HashSet<>();
    private final Set<Task> mTaskHolder = new HashSet<>();
    private Task mCurTask;


    public Starter() {

        mTasks.clear();
        mTaskHolder.clear();
        mCurTask = null;

        ShowMainPage showMainPage = new ShowMainPage();
        ShowSplash showSplash = new ShowSplash();
        InitCore initCore = new InitCore();
        PermissionAcquire permissionAcquire = new PermissionAcquire();


        add(showSplash).depends(permissionAcquire)
                .add(showMainPage).depends(showSplash, initCore)
                .add(initCore).depends(permissionAcquire);

        mTaskHolder.addAll(mTasks);
    }

    public static void startup() {
        if (sInstance == null) {
            sInstance = new Starter();
        }

        sInstance.check();
    }

    private void check() {

        if (!mTasks.isEmpty()) {
            Set<Task> ready = new HashSet<>();
            for (Task task : mTasks) {
                if (task.prevs.isEmpty()) {
                    ready.add(task);
                }
            }

            if (!ready.isEmpty()) {
                mTasks.removeAll(ready);

                for (Task task : ready) {
                    Log.d(TAG, "execute: " + task.getClass().getName());
                    task.action();
                }
            }
        } else {
            sInstance = null;
            mTaskHolder.clear();
            if (sFirstLaunch) {
                MsgDispatcher.get().dispatch(Messege.START_UP_FINISHED);
                sFirstLaunch = false;
            }
        }
    }

    private void onFinish(Task task) {
        if (!task.nexts.isEmpty()) {
            for (Task s : task.nexts) {
                s.prevs.remove(task);
            }
        }


        check();
    }

    public Starter add(Task task) {
        if (!mTasks.contains(task)) {
            mTasks.add(task);
            task.starter = this;
        }

        mCurTask = task;

        return this;
    }

    public Starter depends(Task... tasks) {
        if (mCurTask == null) {
            throw new IllegalStateException("tasks can not depends on empty tasks!");
        }

        if (tasks != null && tasks.length > 0) {
            for (Task task : tasks) {
                mCurTask.depends(task);
                task.nexts(mCurTask);

                if (!mTasks.contains(task)) {
                    mTasks.add(task);
                    task.starter = this;
                }
            }
        }

        return this;
    }

    public static abstract class Task {

        private Starter starter;
        private final Set<Task> nexts = new HashSet<>();
        private final Set<Task> prevs = new HashSet<>();

        protected abstract int start();

        private void action() {

            try {
                int ret = start();
                if (ret == 0) {
                    finish();
                }

            } catch (Throwable t) {
                Log.e(TAG, "task execute exception: \n" + Log.getStackTraceString(t));
                t.printStackTrace();
            }

        }

        protected void finish() {
            starter.onFinish(this);
        }

        private void depends(Task task) {
            prevs.add(task);
        }

        private void nexts(Task task) {
            nexts.add(task);
        }
    }

}
