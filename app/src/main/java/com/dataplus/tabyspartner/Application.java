package com.dataplus.tabyspartner;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

public class Application extends android.app.Application {
    private static Application mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        // Creating an extended library configuration.
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("d4b5ea55-eca1-45d0-a595-4b6158718ac8").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);
    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

}
