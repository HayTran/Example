package com.a20170208.tranvanhay.respberry3;

import android.app.Activity;
import android.util.Log;

import com.google.android.things.contrib.driver.button.Button;

import java.io.IOException;

/**
 * Created by Tran Van Hay on 3/4/2017.
 */

public class DoorbellActivity extends Activity{
    /**
     * The GPIO pin to activate for button presses
     *
     *
     * cho nao em login
     */
    private final String BUTTON_GPIO_PIN = "BMC20";

    /**
     * Driver for the doorbell button;
     */
    private Button mButton;

    /**
     * Initializes button driver, which will report physical button presses.
     */

    private void initializeDoorbellButton() {
        try {
            mButton = new Button(BUTTON_GPIO_PIN,
                    Button.LogicState.PRESSED_WHEN_LOW);
            mButton.setOnButtonEventListener(mButtonCallback);
        } catch (IOException e) {
            Log.e(this.getLocalClassName(), "button driver error", e);
        }
    }

    /**
     * Callback for button events.
     */
    private Button.OnButtonEventListener mButtonCallback =
            new Button.OnButtonEventListener() {
                @Override
                public void onButtonEvent(Button button, boolean pressed) {
                    if (pressed) {
                        // Doorbell rang!
                        Log.d("DoorbellActivity", "button pressed");
                    }
                }
            };
}
