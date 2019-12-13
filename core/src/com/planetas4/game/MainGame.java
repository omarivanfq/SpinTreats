package com.planetas4.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Ads.AdsController;
import com.planetas4.game.Ads.DummyAdsController;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Managers.LevelsManager;
import com.planetas4.game.Screens.AboutScreen;
import com.planetas4.game.Screens.FinalScreen;
import com.planetas4.game.Screens.GameScreen;
import com.planetas4.game.Screens.HomeScreen;
import com.planetas4.game.Screens.LevelsScreen;
import com.planetas4.game.Screens.LoadingScreen;
import com.planetas4.game.Screens.SettingsScreen;
import com.planetas4.game.Screens.ThankYouScreen;

public class MainGame extends Game {

	public Screen homeScreen;
	public Viewport viewport;
	public FinalScreen finalScreen;
	public LevelsScreen levelsScreen;
	public GameScreen gameScreen;
	public AboutScreen aboutScreen;
	public SettingsScreen settingsScreen;
	public ThankYouScreen thankYouScreen;
	public AssetManager manager;
	private AdsController adsController;

	public MainGame(AdsController adsController){
		if (adsController != null) {
			this.adsController = adsController;
		} else {
			this.adsController = new DummyAdsController();
		}
	}

	@Override
	public void create() {
		loadAssetManager();
		setScreen(new LoadingScreen(MainGame.this)); // we first go to the loading screen.. once there we call prepare()
	}

	/* function to load the stuff needed to start the game */
	public void prepare() {
		LevelsManager.unlockAll(); // all the levels are available to be played from start
		//	LevelsManager.unlockAllSections();
		viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		loadScreens();
	}

	/* loading the screens of the game */
	private void loadScreens() {
		aboutScreen = new AboutScreen(this, manager);
		gameScreen = new GameScreen(MainGame.this, manager);
		finalScreen = new FinalScreen(MainGame.this, manager);
		levelsScreen = new LevelsScreen(MainGame.this, manager);
		thankYouScreen = new ThankYouScreen(MainGame.this, manager);
		settingsScreen = new SettingsScreen(MainGame.this, manager);
		homeScreen = new HomeScreen(MainGame.this, manager);
	}

	/* function to load all the assets for the game */
	public void loadAssetManager() {
        manager = new AssetManager();
		manager.load("music/background-music.mp3", Music.class);
        manager.load("sounds/collect3.mp3", Sound.class);
		manager.load("sounds/pop.mp3", Sound.class);
		manager.load("fondo.jpg", Texture.class);
		manager.load("selection-bg.jpg", Texture.class);
		manager.load("planets/planeta1.png", Texture.class);
        manager.load("planets/planeta2.png", Texture.class);
        manager.load("planets/planeta3.png", Texture.class);
        manager.load("planets/planeta4.png", Texture.class);
        manager.load("planets/planeta5.png", Texture.class);
		manager.load("planets/planeta4.png", Texture.class);
		manager.load("planets/planeta7.png", Texture.class);
		manager.load("cats/cat-01.png", Texture.class);
		manager.load("cats/cat-02.png", Texture.class);
		manager.load("cats/cat-03.png", Texture.class);
		manager.load("cats/cat-04.png", Texture.class);
		manager.load("cats/cat-05.png", Texture.class);
		manager.load("cats/cat-06.png", Texture.class);
		manager.load("cats/cat-07.png", Texture.class);
		manager.load("cats/cat-08.png", Texture.class);
		manager.load("cats/cat-09.png", Texture.class);
		manager.load("cats/cat-10.png", Texture.class);
		manager.load("cats/cat-11.png", Texture.class);
		manager.load("cats/cat-12.png", Texture.class);
		manager.load("cats/cat-13.png", Texture.class);
		manager.load("cats/cat-14.png", Texture.class);
		manager.load("cats/cat-15.png", Texture.class);
		manager.load("cats/cat-16.png", Texture.class);
		manager.load("cats/cat-17.png", Texture.class);
		manager.load("cats/cat-18.png", Texture.class);
		manager.load("cats/cat-19.png", Texture.class);
		manager.load("cats/cat-incog.png", Texture.class);
		manager.load("planets/pico.png", Texture.class);
		manager.load("planets/planeta6.png", Texture.class);
		manager.load("planets/planeta_6_glow.png", Texture.class);
		manager.load("planets/planeta_7_glow.png", Texture.class);
		manager.load("planets/planeta_glow.png", Texture.class);
		manager.load("iconos/back.png", Texture.class);
		manager.load("iconos/next.png", Texture.class);
		manager.load("iconos/pausa.png", Texture.class);
		manager.load("iconos/previous.png", Texture.class);
		manager.load("iconos/checkmark.png", Texture.class);
		manager.load("iconos/x.png", Texture.class);
		manager.load("sombra.png", Texture.class);
		manager.load("knob.png", Texture.class);
		manager.load("blanco_transparente.png", Texture.class);
		manager.load("sombra_oscura.png", Texture.class);
		manager.load("treats/treat.png", Texture.class);
		manager.load("treats/treatTiempo.png", Texture.class);
		manager.load("treats/treatEfecto.png", Texture.class);
		manager.load("treats/treatRevela.png", Texture.class);
		manager.load("treats/treatHabilita.png", Texture.class);
		manager.load("destellos/spark.pack" , TextureAtlas.class);
		manager.load("destellos/spark_efecto.pack" , TextureAtlas.class);
		manager.load("destellos/spark_revela.pack" , TextureAtlas.class);
		manager.load("destellos/spark_habilita.pack" , TextureAtlas.class);
		manager.load("destellos/spark_tiempo.pack" , TextureAtlas.class);
		manager.load("tail_animation/tail-anim.txt" , TextureAtlas.class);
		manager.load("borde.png", Texture.class);
		manager.load("cambiar-direccion.png", Texture.class);
		manager.load("mantener-presionado.png", Texture.class);
		manager.load("rotar-p6.png", Texture.class);
		manager.load("planeta3_explosion/p3_explosion.txt", TextureAtlas.class);
		manager.load("planets/explosion/explosion.txt", TextureAtlas.class);
		manager.load("iconos/heart_icon.png", Texture.class);
		manager.load("fondo_azul_claro.png", Texture.class);
		manager.load("fondo_verde.png", Texture.class);
	}

	public void showAd() {
		if (adsController.isWifiConnected()) {
			adsController.showInterstitialAd(new Runnable() {
				@Override
				public void run() { }
			});
		} else {
			System.out.println("Interstitial ad not (yet) loaded");
		}
	}

	@Override
	public void render() {
		super.render();
	}

	public AssetManager getManager() { return  manager; }

	@Override
	public void dispose() {
		super.dispose();
		// DISPOSE ALL RESOURCES
		getScreen().dispose();
		Gdx.app.exit();
	}


}
