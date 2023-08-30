package com.wavecell.sample.app.injection.components;

import com.wavecell.sample.app.PushListenerService;
import com.wavecell.sample.app.WavecellApplication;
import com.wavecell.sample.app.injection.modules.CallModule;
import com.wavecell.sample.app.injection.modules.IncomingCallModule;
import com.wavecell.sample.app.injection.modules.MainModule;
import com.wavecell.sample.app.injection.modules.NotificationModule;
import com.wavecell.sample.app.injection.modules.RegisterModule;
import com.wavecell.sample.app.injection.modules.SampleAppModule;
import com.wavecell.sample.app.injection.scopes.SampleApplicationScope;
import com.wavecell.sample.app.network.GetJWTTokenWorker;
import com.wavecell.sample.app.presentation.notifications.CallService;

import dagger.Component;

@SampleApplicationScope
@Component(modules = {
        SampleAppModule.class,
        NotificationModule.class})
public interface SampleAppComponent {
    void inject(WavecellApplication application);
    void inject(PushListenerService pushListenerService);
    void inject(CallService callService);
    void inject(GetJWTTokenWorker worker);

    MainComponent mainComponent(MainModule mainModule);
    RegisterComponent registerComponent(RegisterModule registerModule);
    CallComponent callComponent(CallModule callModule);
    IncomingCallComponent incomingCallComponent(IncomingCallModule module);
}
