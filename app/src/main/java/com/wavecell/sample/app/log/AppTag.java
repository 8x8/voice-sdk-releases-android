package com.wavecell.sample.app.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppTag {
    private final String featureTag;
    private final String componentTag;
    private final String className;


    private AppTag(String featureTag, String componentTag, String className) {
        this.featureTag = featureTag;
        this.componentTag = componentTag;
        this.className = className;
    }

    String getFeatureTag() {
        return featureTag;
    }

    String getComponentTag() {
        return componentTag;
    }

    String getClassName() {
        return className;
    }

    public static AppTag make(@NonNull @AppFeatureTag String featureName,
                            @Nullable @AppComponentTag String componentName,
                            @NonNull Class klass) {
        return new AppTag(featureName, componentName, klass.getSimpleName());
    }

    public static AppTag make(@NonNull @AppFeatureTag String featureName, @NonNull Class klass) {
        return make(featureName, null, klass);
    }
}
