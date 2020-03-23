package a2.mobile.mobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class PasscodeHandler {
    private int PASSCODE_KEY;

    private Context context;
    private Intent activityToStart;

    PasscodeHandler(Context context, Intent activityToStart) {
        this.context = context;
        this.activityToStart = activityToStart;

        PASSCODE_KEY = Integer.parseInt(context.getResources()
                .getString(R.string.passcode_key)
        );
    }

    /**
     * Reveal the hidden passcode field and hide the authentication options list.
     */
    void onPasscodeOptionClick() {
        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        RecyclerView authenticationRecyclerView = rootView.findViewById(R.id.authentication_recycler_view);
        authenticationRecyclerView.setVisibility(View.GONE);

        LinearLayout passcodeHolder = rootView.findViewById(R.id.passcode_holder);
        passcodeHolder.setVisibility(View.VISIBLE);
        passcodeHolder.requestFocus();

        if (LogIn.INPUT_METHOD_MANAGER != null) {
            LogIn.INPUT_METHOD_MANAGER.showSoftInput(
                    passcodeHolder.findViewById(R.id.passcode_input),
                    InputMethodManager.SHOW_IMPLICIT
            );
        }

        EditText passcodeInput = rootView.findViewById(R.id.passcode_input);
        passcodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1 && charSequence != "") {
                    boolean isPasscodeValid = isPasscodeValid(passcodeInput.getText().toString());

                    if (isPasscodeValid) {
                        context.startActivity(activityToStart);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passcodeInput.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.FLAG_EDITOR_ACTION) {
                if (LogIn.INPUT_METHOD_MANAGER != null) {
                    LogIn.INPUT_METHOD_MANAGER.hideSoftInputFromWindow(
                            passcodeInput.getWindowToken(),
                            0
                    );
                }

                boolean isPasscodeValid = isPasscodeValid(passcodeInput.getText().toString());
                if (isPasscodeValid) {
                    context.startActivity(activityToStart);
                }
            }

            return false;
        });
    }

    /**
     * Is the provided passcode matching the stored one.
     *
     * @param inputText The current passcode
     * @return Whether the passcode is valid or not.
     */
    private Boolean isPasscodeValid(@NonNull String inputText) {
        if (inputText.length() >= 1) {
            int currentPasscode = Integer.parseInt(inputText);

            return currentPasscode == PASSCODE_KEY;
        }

        return false;
    }
}
