package com.dvx.network;

import android.os.AsyncTask;

import com.dvx.network.interfaces.INetWorker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by andoop on 2016/11/1.
 */

public class WorkHouse {

    private ExecutorService executorService=Executors.newFixedThreadPool(4);

    public void add(final INetWorker netWorker) {

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                netWorker.work();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                netWorker.packResult();
            }
        }.executeOnExecutor(executorService);

    }
}
