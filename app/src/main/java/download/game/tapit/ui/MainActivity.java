package download.game.tapit.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import download.game.tapit.R;
import download.game.tapit.databinding.ActivityMainBinding;
import download.game.tapit.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    public static final int NUMBER_GENRATED_REQUEST_CODE = -1;
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mainActivityViewModel = new ViewModelProvider(MainActivity.this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication()))
                .get(MainActivityViewModel.class);

        binding.scorecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                score = mainActivityViewModel.getScore();
                if (!mainActivityViewModel.getIsGameRunning().getValue()) {
                    mainActivityViewModel.startStopGame();
                    binding.scorecard.setText("Score : " + score);
                }
            }
        });

        mainActivityViewModel.getIsGameRunning().observe(this,
                new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        Log.d(TAG, "onChanged: getIsGameRunning " + aBoolean);
                        if (aBoolean) {
                            //game is running
                            mainActivityViewModel.startRandomColorSelectionService(getApplicationContext());
                        } else {
                            //game is stopped
                            mainActivityViewModel.stopRandomColorSelectionService(getApplicationContext());
                        }

                    }
                });

        mainActivityViewModel.getCurrentGreyBoxNumber().observe(this,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        Log.d(TAG, "onChanged: integer ");
                        setColorToGrey(integer);
                    }
                });
    }

    private void setColorToGrey(Integer integer) {
        Log.d(TAG, "setColorToGrey: integer " + integer);
        setPreviousToOriginalColor();
        if (mainActivityViewModel.getPreviousGreyBlock() != -1 && stopGameIfTapMissed()) {
            return;
        }
        //move new to grey state and set previous to new value
        mainActivityViewModel.setPreviousGreyBlock(integer);
        switch (integer) {
            case 1:
                binding.firstBlock.setImageResource(R.drawable.grey_shape);
                mainActivityViewModel.setTapped(false);
                break;
            case 2:
                binding.secondBlock.setImageResource(R.drawable.grey_shape);
                mainActivityViewModel.setTapped(false);
                break;
            case 3:
                binding.thirdBlock.setImageResource(R.drawable.grey_shape);
                mainActivityViewModel.setTapped(false);
                break;
            case 4:
                binding.fourthBlock.setImageResource(R.drawable.grey_shape);
                mainActivityViewModel.setTapped(false);
                break;
        }
    }

    private boolean stopGameIfTapMissed() {
        if (!mainActivityViewModel.isTapped()) {
            Log.d(TAG, "stopGameIfTapMissed: tap missed ");
            gameOver();
            return true;
        }
        return false;
    }

    private void setPreviousToOriginalColor() {
        Log.d(TAG, "setColorToGrey: mainActivityViewModel.getPreviousGreyBlock() " + mainActivityViewModel.getPreviousGreyBlock());
        switch (mainActivityViewModel.getPreviousGreyBlock()) {
            case 1:
                binding.firstBlock.setImageResource(R.drawable.orange_shape);
                break;
            case 2:
                binding.secondBlock.setImageResource(R.drawable.blue_shape);
                break;
            case 3:
                binding.thirdBlock.setImageResource(R.drawable.yellow_shape);
                break;
            case 4:
                binding.fourthBlock.setImageResource(R.drawable.green_shape);
                break;
        }
    }

    public void colorDrawableTapped(View v) {
        Log.d(TAG, "colorDrawableTapped: v.getId() " + v.getId());
        Log.d(TAG, "colorDrawableTapped: mainActivityViewModel.getCurrentGreyBoxNumber().getValue() " + mainActivityViewModel.getCurrentGreyBoxNumber().getValue());
        if (!mainActivityViewModel.isTapped() && mainActivityViewModel.getIsGameRunning().getValue())
            switch (v.getId()) {
                case R.id.first_block:
                    if (mainActivityViewModel.getCurrentGreyBoxNumber().getValue() != 1) {
                        Log.d(TAG, "colorDrawableTapped: game over 1");
                        //game over
                        gameOver();
                    } else {
                        mainActivityViewModel.setScore(++score);
                        binding.scorecard.setText("Score : " + score);
                        mainActivityViewModel.setTapped(true);
                    }
                    break;
                case R.id.second_block:
                    if (mainActivityViewModel.getCurrentGreyBoxNumber().getValue() != 2) {
                        //game over
                        Log.d(TAG, "colorDrawableTapped: game over 2");
                        gameOver();
                    } else {
                        mainActivityViewModel.setScore(++score);
                        binding.scorecard.setText("Score : " + score);
                        mainActivityViewModel.setTapped(true);
                    }
                    break;
                case R.id.third_block:
                    if (mainActivityViewModel.getCurrentGreyBoxNumber().getValue() != 3) {
                        //game over
                        Log.d(TAG, "colorDrawableTapped: game over 3");
                        gameOver();
                    } else {
                        mainActivityViewModel.setScore(++score);
                        binding.scorecard.setText("Score : " + score);
                        mainActivityViewModel.setTapped(true);
                    }
                    break;
                case R.id.fourth_block:
                    if (mainActivityViewModel.getCurrentGreyBoxNumber().getValue() != 4) {
                        //game over
                        Log.d(TAG, "colorDrawableTapped: game over 4");
                        gameOver();
                    } else {
                        mainActivityViewModel.setScore(++score);
                        binding.scorecard.setText("Score : " + score);
                        mainActivityViewModel.setTapped(true);
                    }
                    break;
            }
    }

    private void gameOver() {
        //game over
        setPreviousToOriginalColor();
        mainActivityViewModel.resetValues();
        showGameOverDialogueBox();
    }

    private void showGameOverDialogueBox() {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("GAME OVER. Your Score is " + score)
                .setCancelable(false)
                .setPositiveButton("Restart Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        score = 0;
                        binding.scorecard.setText("Score : " + score);
                        mainActivityViewModel.startStopGame();
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}