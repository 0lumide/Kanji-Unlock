package co.mide.kanjiunlock;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.Preference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SelectPin extends AppCompatActivity {
    private EditText pinTextEdit;
    private TextView instruction;
    private int state;
    private int pin;
    private Button okButton;
    private String prevInstruction;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pin);
        pinTextEdit = (EditText) findViewById(R.id.pinEditText);
        instruction = (TextView) findViewById(R.id.instructions);
        okButton = (Button) findViewById(R.id.okButton);
        prevInstruction = "";
        preferences = getSharedPreferences(AppConstants.PREF_NAME, MODE_PRIVATE);
        state = 0;
        Log.v("Pin","Started");
        if(preferences.getBoolean(AppConstants.PIN_SET, false)) {
            state = -1;
            pin = preferences.getInt(AppConstants.PIN, 0);
            instruction.setText(R.string.pin_reset_instruction);
        }
        pinTextEdit.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

               @Override
               public void afterTextChanged(Editable editable) {
                   if(editable.length()== 4){
                       //enable okay button
                       prevInstruction = instruction.getText().toString();
                       Resources res = getResources();
                       String ins = res.getString(R.string.pin_continue_instruction, res.getString(R.string.ok_button));
                       instruction.setText(ins);
                       okButton.setEnabled(true);
                   }
                   else{
                       if(!prevInstruction.equals(""))
                           instruction.setText(prevInstruction);
                       okButton.setEnabled(false);//disable ok button
                   }
               }
           }
        );
        pinTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    handlePinSet();
                }
                return true;
            }
        });
    }

    private void handlePinSet(){
        if(pinTextEdit.getText().length() != 4){
            return;
        }
        if(state == -1){
            int newPin = Integer.parseInt(pinTextEdit.getText().toString());
            if(newPin == pin){
                preferences.edit().putBoolean(AppConstants.PIN_SET, false).apply();
                if(getIntent().getBooleanExtra(AppConstants.CLEAR_PIN, false)){
                    finish();
                }
                else {
                    pinTextEdit.setText("");
                    instruction.setText(R.string.pin_begin_instruction);
                    state = 0;
                }
            }else{
                pinTextEdit.setText("");
                instruction.setText(R.string.pin_try_again_instruction);
            }
        }
        else if(state == 0){
            pin = Integer.parseInt(pinTextEdit.getText().toString());
            pinTextEdit.setText("");
            instruction.setText(R.string.pin_confirm_instruction);
            preferences.edit().putBoolean(AppConstants.PIN_SET, false).apply();
            prevInstruction = "";
            state = 1;
        }
        else if(state == 1){
            int enteredPin = Integer.parseInt(pinTextEdit.getText().toString());
            if(pin == enteredPin) {
                Toast.makeText(this, R.string.pin_correct_instruction, Toast.LENGTH_SHORT).show();
                preferences.edit().putInt(AppConstants.PIN, pin).apply();
                preferences.edit().putBoolean(AppConstants.PIN_SET, true).apply();
                finish();
            }
            else{
                pinTextEdit.setText("");
                instruction.setText(R.string.pin_try_again_instruction);
                prevInstruction = "";
                state = 1;
            }
        }
    }

    public void okButtonPressed(View v){
        handlePinSet();
    }

    public void cancelButtonPressed(View v){
        finish();
    }
}
