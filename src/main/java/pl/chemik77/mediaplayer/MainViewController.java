package pl.chemik77.mediaplayer;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;

public class MainViewController {
	@FXML
	private VBox vbox;
	@FXML
	private MediaView mediaView;
	@FXML
	private Slider timeSlider;
	@FXML
	private Button openButton;
	@FXML
	private Button rewindButton;
	@FXML
	private Button playButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button stopButton;
	@FXML
	private Button forwardButton;
	@FXML
	private Button muteButton;
	@FXML
	private Slider volumeSlider;

	private Media media;
	private MediaPlayer mediaPlayer;
	private String filePath;
	private Double previousVolume;
	private Stage primaryStage;

	@FXML
	private void initialize() {
		this.primaryStage = Main.getPrimaryStage();
		disableButtons(true);

	}

	// Disables buttons
	private void disableButtons(boolean b) {
		this.playButton.setDisable(b);
		this.rewindButton.setDisable(b);
		this.pauseButton.setDisable(b);
		this.stopButton.setDisable(b);
		this.forwardButton.setDisable(b);
		this.muteButton.setDisable(b);
	}

	// Sets function fullScreen after mouse clicked two times
	private void setFullScreen() {
		Scene scene = primaryStage.getScene();
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					if (primaryStage.isFullScreen()) {
						primaryStage.setFullScreen(false);
					} else {
						primaryStage.setFullScreen(true);
					}
				}
			}
		});
	}

	// Creates and sets animation of hiding and showing menu under video
	private void setAnimation() {
		Timeline slideIn = new Timeline();
		Timeline slideOut = new Timeline();

		KeyValue kv1 = new KeyValue(vbox.translateYProperty(), 70);
		KeyValue kv2 = new KeyValue(vbox.opacityProperty(), 0.0);
		KeyValue kv3 = new KeyValue(vbox.translateYProperty(), 0);
		KeyValue kv4 = new KeyValue(vbox.opacityProperty(), 0.9);
		Duration dur1 = Duration.millis(0);
		Duration dur2 = Duration.millis(1000);
		slideIn.getKeyFrames().addAll(new KeyFrame(dur1, kv1, kv2), new KeyFrame(dur2, kv3, kv4));
		slideOut.getKeyFrames().addAll(new KeyFrame(dur1, kv3, kv4), new KeyFrame(dur2, kv1, kv2));

		primaryStage.getScene().setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				slideIn.play();
			}
		});

		primaryStage.getScene().setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				slideOut.play();
			}
		});

	}

	// Sets control of application when media Player has Status.READY
	private void readyMediaPlayer() {
		this.mediaPlayer.setOnReady(new Runnable() {
			@Override
			public void run() {
				setVolumeControl();
				setVideoToScene();
				setTimeControl();
				setAnimation();
			}
		});
	}

	// Sets total duration on time Slider;
	// adds listener on time Slider and changes point in video when mouse is
	// pressed or dragged
	private void setTimeControl() {
		this.timeSlider.setMin(0.0);
		this.timeSlider.setValue(0.0);
		this.timeSlider.setMax(this.mediaPlayer.getTotalDuration().toSeconds());

		this.mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {

			@Override
			public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
				timeSlider.setValue(newValue.toSeconds());
			}
		});

		this.timeSlider.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
			}
		});

		this.timeSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
			}
		});
	}

	// Sets images on mute Button and changes when mute is on or off;
	// adds listener on volume Slider and changes volume of a video
	private void setVolumeControl() {
		Image volume = new Image(this.getClass().getResourceAsStream("/image/volume-button.png"));
		Image mute = new Image(this.getClass().getResourceAsStream("/image/mute-button.png"));
		this.volumeSlider.setValue(this.mediaPlayer.getVolume() * 100);
		this.volumeSlider.valueProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				mediaPlayer.setVolume(volumeSlider.getValue() / 100);
				if (volumeSlider.getValue() == 0.0) {
					muteButton.setGraphic(new ImageView(mute));
				} else {
					muteButton.setGraphic(new ImageView(volume));
				}
			}
		});

		if (this.mediaPlayer.getVolume() == 0) {
			this.muteButton.setGraphic(new ImageView(mute));
		} else {
			this.muteButton.setGraphic(new ImageView(volume));
		}
	}

	// Sets MediaView from file path
	private void setMediaFromPath() {
		if (filePath != null) {
			this.media = new Media(filePath);
			this.mediaPlayer = new MediaPlayer(this.media);
			this.mediaView.setMediaPlayer(mediaPlayer);
		}
	}

	// Sets proper size of window from video
	protected void setVideoToScene() {
		DoubleProperty widthProp = mediaView.fitWidthProperty();
		DoubleProperty heightProp = mediaView.fitHeightProperty();

		widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

	}

	// Event Listener on Button[#openButton].onAction
	@FXML
	public void openButtonOnAction(ActionEvent event) {
		if (filePath != null) {
			mediaPlayer.pause();
		}
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video file", "*.mp4");
		fileChooser.getExtensionFilters().add(filter);
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			this.filePath = file.toURI().toString();
			setMediaFromPath();
			readyMediaPlayer();
			setFullScreen();
			disableButtons(false);
			setTitlePlayer(file.getName());
			mediaPlayer.play();
		}

	}

	private void setTitlePlayer(String name) {
		this.primaryStage.setTitle(name);
	}

	// Event Listener on Button[#rewindButton].onAction
	@FXML
	public void rewindButtonOnAction(ActionEvent event) {
		this.mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(5)));
	}

	// Event Listener on Button[#playButton].onAction
	@FXML
	public void playButtonOnAction(ActionEvent event) {
		if (mediaPlayer.getCurrentTime().greaterThanOrEqualTo(mediaPlayer.getStopTime())) {
			mediaPlayer.seek(mediaPlayer.getStartTime());
		}
		this.mediaPlayer.play();
	}

	// Event Listener on Button[#pauseButton].onAction
	@FXML
	public void pauseButtonOnAction(ActionEvent event) {
		this.mediaPlayer.pause();
	}

	// Event Listener on Button[#stopButton].onAction
	@FXML
	public void stopButtonOnAction(ActionEvent event) {
		this.mediaPlayer.stop();
	}

	// Event Listener on Button[#forwardButton].onAction
	@FXML
	public void forwardButtonOnAction(ActionEvent event) {
		this.mediaPlayer.seek(this.mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
	}

	// Event Listener on Button[#muteButton].onAction
	@FXML
	public void muteButtonOnAction(ActionEvent event) {
		if (this.mediaPlayer.getVolume() != 0) {
			this.previousVolume = this.mediaPlayer.getVolume();
			this.mediaPlayer.setVolume(0);
		} else {
			this.mediaPlayer.setVolume(this.previousVolume);
		}
		setVolumeControl();
	}
}
