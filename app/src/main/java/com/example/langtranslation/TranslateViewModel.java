package com.example.langtranslation;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


public class TranslateViewModel extends AndroidViewModel {
    MutableLiveData<Language> sourceLang = new MutableLiveData<>();
    MutableLiveData<Language> targetLang = new MutableLiveData<>();
    MutableLiveData<String> sourceText = new MutableLiveData<>();
    MediatorLiveData<ResultOrError> translatedText = new MediatorLiveData<>();

    public TranslateViewModel(@NonNull Application application){
        super(application);

        //create a translation result or error object
        final OnCompleteListener<String> processTranslation = new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    translatedText.setValue(new ResultOrError(task.getResult(), null));
                } else {
                    translatedText.setValue(new ResultOrError(null, task.getException()));
                }

            }
        };

        //Start translation if input text changes
        translatedText.addSource(sourceText, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                translate().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        translatedText.setValue(new ResultOrError(task.getResult(), null));
                    }
                });
//                translate().addOnSuccessListener(new OnSuccessListener<String>() {
//                    @Override
//                    public void onSuccess(String s) {
//                        translatedText.setValue(new ResultOrError(s, null));
//
//                    }
//                });
            }
        });

    }

    public Task<String> translate() {
        final String text = sourceText.getValue();
        final Language source = sourceLang.getValue();
        final Language target = targetLang.getValue();

            if (source == null || target == null || text.isEmpty()) {
                return Tasks.forResult("");
            }

        int sourceLangCode = FirebaseTranslateLanguage.languageForLanguageCode(source.getCode());
        int targetLangCode = FirebaseTranslateLanguage.languageForLanguageCode(target.getCode());

        FirebaseTranslatorOptions options = new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(sourceLangCode)
                .setTargetLanguage(targetLangCode)
                .build();

        final FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();

        return translator.downloadModelIfNeeded(conditions).continueWithTask(
                new Continuation<Void, Task<String>>(){
                    @Override
                    public Task<String> then(@NonNull Task<Void> task) throws Exception {
                        if (task.isSuccessful()) {
                            return translator.translate(text);
                        } else {
                            Exception e = task.getException();
                            if (e == null) {
                                e = new Exception(getApplication().getString(R.string.unknown_error));
                            }
                            return Tasks.forException(e);
                        }
                    }
        });
    }
}
