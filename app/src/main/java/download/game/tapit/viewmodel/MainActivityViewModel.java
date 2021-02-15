package download.game.tapit.viewmodel;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import download.game.tapit.services.RandomColorSelectionService;
import download.game.tapit.utility.ServiceUtility;

import static download.game.tapit.ui.MainActivity.NUMBER_GENRATED_REQUEST_CODE;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";

    private final MutableLiveData<Boolean> isGameRunning;
    private final MutableLiveData<Integer> currentGreyBoxNumber;
    private final ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Log.d(TAG, "onReceiveResult: resultCode " + resultCode);
            if(resultCode == NUMBER_GENRATED_REQUEST_CODE)
                currentGreyBoxNumber.setValue(resultData.getInt("random_int"));
        }
    };
    private int previousGreyBlock;
    private int score = 0;
    private boolean tapped;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        isGameRunning = new MutableLiveData<>(false);
        currentGreyBoxNumber = new MutableLiveData<>(-1);
        previousGreyBlock = currentGreyBoxNumber.getValue();
    }


    public void startStopGame() {
        isGameRunning.setValue(!isGameRunning.getValue());
    }

    public LiveData<Boolean> getIsGameRunning() {
        return isGameRunning;
    }

    public LiveData<Integer> getCurrentGreyBoxNumber() {
        return currentGreyBoxNumber;
    }

    public void startRandomColorSelectionService(Context context) {
        if (!ServiceUtility.isMyServiceRunning(RandomColorSelectionService.class, context))
            RandomColorSelectionService.startService(RandomColorSelectionService.class, context, resultReceiver);
    }

    public void stopRandomColorSelectionService(Context context) {
        if (ServiceUtility.isMyServiceRunning(RandomColorSelectionService.class, context))
            RandomColorSelectionService.stopService(RandomColorSelectionService.class, context);
    }

    public int getPreviousGreyBlock() {
        return previousGreyBlock;
    }

    public void setPreviousGreyBlock(int previousGreyBlock) {
        this.previousGreyBlock = previousGreyBlock;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isTapped() {
        return tapped;
    }

    public void setTapped(boolean tapped) {
        this.tapped = tapped;
    }

    public void resetValues(){
        startStopGame();
        setScore(0);
        setPreviousGreyBlock(-1);
        currentGreyBoxNumber.setValue(-1);
        setTapped(false);
    }
}
