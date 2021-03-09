package com.example.langtranslation;

import androidx.annotation.Nullable;

/**
 * Holds the result of the translation or any error.
 */
public class ResultOrError {
    final @Nullable
    String result;
    final @Nullable
    Exception error;

    ResultOrError(@Nullable String result, @Nullable Exception error) {
        this.result = result;
        this.error = error;
    }
}

