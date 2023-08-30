package com.wavecell.sample.app.injection;

public interface SampleAppComponentHolder {

    <C> C getComponent(Class<C> clz, Object... args);

    void clearComponent(Class clz);
}
