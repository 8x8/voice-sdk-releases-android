package com.wavecell.sample.app.injection;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import com.wavecell.sample.app.injection.components.CallComponent;
import com.wavecell.sample.app.injection.components.DaggerSampleAppComponent;
import com.wavecell.sample.app.injection.components.IncomingCallComponent;
import com.wavecell.sample.app.injection.components.MainComponent;
import com.wavecell.sample.app.injection.components.RegisterComponent;
import com.wavecell.sample.app.injection.components.SampleAppComponent;
import com.wavecell.sample.app.injection.modules.CallModule;
import com.wavecell.sample.app.injection.modules.IncomingCallModule;
import com.wavecell.sample.app.injection.modules.MainModule;
import com.wavecell.sample.app.injection.modules.RegisterModule;
import com.wavecell.sample.app.injection.modules.SampleAppModule;
import java.util.HashMap;

public class SampleAppComponentContainer implements SampleAppComponentHolder {

    @VisibleForTesting
    public HashMap<String, Object> container;
    private final Context context;

    public SampleAppComponentContainer(Context context) {
        this.context = context;
        this.container = new HashMap<>();
    }

    @VisibleForTesting
    public <C> void cacheComponent(Class<C> clz, C component) {
        container.put(clz.getCanonicalName(), component);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public synchronized <C> C getComponent(Class<C> clz, Object... args) {
        String classParamName = getContainerKey(clz);

        C component = (C) container.get(classParamName);

        boolean shouldCache = true;

        if (component == null) {
            if (SampleAppComponent.class == clz) {
                component = (C) buildAppComponent(context);
            } else if (MainComponent.class == clz) {
                AppCompatActivity activity = (AppCompatActivity) args[0];
                component = (C) buildMainComponent(activity);
            } else if (RegisterComponent.class == clz) {
                AppCompatActivity activity = (AppCompatActivity) args[0];
                component = (C) buildRegisterComponent(activity);
            } else if (CallComponent.class == clz) {
                AppCompatActivity activity = (AppCompatActivity) args[0];
                component = (C) buildCallComponent(activity);
            } else if (IncomingCallComponent.class == clz) {
                AppCompatActivity activity = (AppCompatActivity) args[0];
                component = (C) buildIncomingCallComponent(activity);
            }

            if (shouldCache) {
                cacheComponent(clz, component);
            }
        }

        return component;
    }

    private <C> String getContainerKey(Class<C> clz) {
        return clz.getCanonicalName();
    }

    @Override
    public void clearComponent(Class clz) {
        if (clz == null) {
            return;
        }

        String canonicalName = getContainerKey(clz);
        container.remove(canonicalName);
    }

    private SampleAppComponent buildAppComponent(Context context) {
        return DaggerSampleAppComponent
                .builder()
                .sampleAppModule(new SampleAppModule(context))
                .build();
    }


    protected <C> C get(Class<C> componentType) {
        return componentType.cast(getComponent(componentType));
    }

    private MainComponent buildMainComponent(AppCompatActivity activity) {
        return getComponent(SampleAppComponent.class).mainComponent(new MainModule(activity));
    }

    private CallComponent buildCallComponent(AppCompatActivity activity) {
        return getComponent(SampleAppComponent.class).callComponent(new CallModule(activity));
    }

    private RegisterComponent buildRegisterComponent(AppCompatActivity activity) {
        return getComponent(SampleAppComponent.class).registerComponent(new RegisterModule(activity));
    }

    private IncomingCallComponent buildIncomingCallComponent(AppCompatActivity activity) {
        return getComponent(SampleAppComponent.class).incomingCallComponent(new IncomingCallModule(activity));
    }
}
