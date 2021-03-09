package com.example.langtranslation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // Exercise 2a: declaration of source_txt & target_txt variable

    private TextInputLayout mSourceText;
    private TextView mTargetText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Exercise 2 b: Intialization of source_txt & target_txt variables

        mSourceText = findViewById(R.id.source_input);
        mTargetText = (TextView) findViewById(R.id.target_text);

        //get translation model and set languages
        final TranslateViewModel viewModel = ViewModelProviders.of(this).get(TranslateViewModel.class);
        viewModel.sourceLang.setValue(new Language("en"));
        viewModel.targetLang.setValue(new Language("fr"));

        //Translate input text as it is typed
        mSourceText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setProgressText(mTargetText);
                viewModel.sourceText.postValue(editable.toString());

            }
        });

        viewModel.translatedText.observe(this, new Observer<ResultOrError>() {
            @Override
            public void onChanged(ResultOrError resultOrError) {
                if (resultOrError.error != null) {
                    mSourceText.setError(resultOrError.error.getLocalizedMessage());
                } else {
                    mTargetText.setText(resultOrError.result);
                }

            }
        });
    }

    private void setProgressText(TextView tv) {
        tv.setText(this.getString(R.string.translate_progress));
    }
}
