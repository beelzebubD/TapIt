package download.game.tapit.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import download.game.tapit.utility.ServiceUtility;

import static download.game.tapit.ui.MainActivity.NUMBER_GENRATED_REQUEST_CODE;

public class RandomColorSelectionService extends Service {

    public static final String RESULT_RECEIVER = "ResultReceiver";
    private static final String TAG = "RandomColorSelectionSer";
    protected ResultReceiver resultReceiver;
    protected int previousGenerated = -1;
    private HandlerThread randomColorSelectorHandlerThread;
    private Handler randomColorSelectorHandler;

    public static void startService(Class<?> clazz, Context context, ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, clazz);
        intent.putExtra(RESULT_RECEIVER, resultReceiver);
        context.startForegroundService(intent);
    }

    public static void stopService(Class<?> clazz, Context context) {
        Intent intent = new Intent(context, clazz);
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        randomColorSelectorHandlerThread = new HandlerThread(RandomColorSelectionService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        resultReceiver = intent.getParcelableExtra(RESULT_RECEIVER);
        ServiceUtility.buildServiceNotification(this, RandomColorSelectionService.class);
        startRandomScreenSelector();
        return START_REDELIVER_INTENT;
    }

    private void startRandomScreenSelector() {
        randomColorSelectorHandlerThread.start();
        randomColorSelectorHandler = new Handler(randomColorSelectorHandlerThread.getLooper());
        randomColorSelectorHandler.post(new Runnable() {
            @Override
            public void run() {
                //generates a number between 1 to 4 for color selection
                int num = (int) (Math.random() * 100) % 4;
                Log.d(TAG, "run: generated num " + num + " previousNum " + previousGenerated);
                if (previousGenerated == -1)
                    previousGenerated = num;
                else if (previousGenerated == num) {
                    num+=1;
                    num%=4;
                    previousGenerated = num;
                } else
                    previousGenerated = num;
                Bundle bundle = new Bundle();
                bundle.putInt("random_int", ++num);
                resultReceiver.send(NUMBER_GENRATED_REQUEST_CODE, bundle);
                randomColorSelectorHandler.postDelayed(this, 1000);
            }
        });
    }

    private void stopRandomScreenSelector() {
        if (randomColorSelectorHandler != null) {
            randomColorSelectorHandler.removeCallbacksAndMessages(null);
            randomColorSelectorHandler = null;
        }
        if (randomColorSelectorHandlerThread != null) {
            randomColorSelectorHandlerThread.quitSafely();
            randomColorSelectorHandlerThread = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called ");
        super.onDestroy();
        stopRandomScreenSelector();
    }
}