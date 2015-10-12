package co.mide.kanjiunlock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CharacterInputDialog extends DialogFragment {
    private AlertDialog alertDialog;
    private boolean isEdit = false;
    private int position = 0;
    private char character = 0;

    public void setEditMode(int position, char character){
        isEdit = true;
        this.character = character;
        this.position = position;
    }

    private void showError(){
        TextView textView = (TextView) this.getDialog().findViewById(R.id.invalid_text);
        textView.setVisibility(View.VISIBLE);
    }

    private void hideError(){
        TextView textView = (TextView) this.getDialog().findViewById(R.id.invalid_text);
        textView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(R.string.character_input_title);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.character_input_dialog, null))
                // Add action buttons
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        alertDialog = (AlertDialog)getDialog();
        if(alertDialog != null)
        {
            EditText editText = (EditText) alertDialog.findViewById(R.id.character_edit_text);
            if(isEdit)
                editText.setText(String.valueOf(character));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    hideError();
                }
            });

            Button positiveButton = (Button) alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText)alertDialog.findViewById(R.id.character_edit_text);
                    char character = editText.getText().charAt(0);
                    if(('a' <= character) && ('z' >= character)){
                        character += ('A' - 'a');
                    }
                    if((editText.getText().length() == 1) && JapCharacter.isValid(character)){
                        hideError();
                        if(!isEdit)
                            ((KanjiUnlock)getActivity()).addCharacter(character);
                        else
                            ((KanjiUnlock) getActivity()).editCharacter(position, character);
                        dismiss();
                    }else{
                        showError();
                    }
                }
            });
        }
    }
}