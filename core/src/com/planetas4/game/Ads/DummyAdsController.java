package com.planetas4.game.Ads;
public class DummyAdsController implements AdsController {
    @Override
    public void showBannerAd() {}
    @Override
    public void hideBannerAd() {}
    @Override
    public boolean isWifiConnected() {return false;}
    @Override
    public void showInterstitialAd(Runnable then) {}
}
