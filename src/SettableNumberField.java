import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SettableNumberField extends TextField {

    private double firstValue;
    private SimpleBooleanProperty set;

    public SettableNumberField(double initialValue) {
        super(String.valueOf(initialValue));
        set = new SimpleBooleanProperty(false);
        firstValue = initialValue;
        createConfirmationHandler();
        setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE){
                setText(String.valueOf(firstValue));
            }
        });
    }

    private void createConfirmationHandler() {
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (isFocused() && !getText().isEmpty()) {
                            selectAll();
                        }
                    }
                });
                if (newValue) {
                    firstValue = Double.parseDouble(getText());
                } else {
                    if (!getText().matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) {
                        setText(String.valueOf(firstValue));
                    } else set.setValue(!set.getValue());
                }
            }
        });
    }

    public double getValue() {
        return Double.parseDouble(this.getText());
    }

    public SimpleBooleanProperty setProperty() {
        return set;
    }
}
