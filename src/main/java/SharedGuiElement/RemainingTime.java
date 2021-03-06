package SharedGuiElement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Created by aalexx on 12/27/15.
 */
public class RemainingTime extends HBox {
    @FXML private Text remainingTime;

    public RemainingTime() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RemainingTime.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setRemainingTime (String remainingTime) {
        this.remainingTime.setText(remainingTime);
    }
}
