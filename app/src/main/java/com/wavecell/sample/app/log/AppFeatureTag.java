package com.wavecell.sample.app.log;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        AppFeatureTag.SAMPLE_APPLICATION,
        AppFeatureTag.VOICE_SDK
})
public @interface AppFeatureTag {
    String SAMPLE_APPLICATION = "SampleApplication";
    String VOICE_SDK = "VoiceSDK";
}

