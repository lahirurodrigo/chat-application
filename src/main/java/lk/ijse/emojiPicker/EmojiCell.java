package lk.ijse.emojiPicker;

import javafx.scene.control.ListCell;
import javafx.scene.text.Text;

public class EmojiCell extends ListCell<String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Text emojiText = new Text(item);
            setGraphic(emojiText);
        }
    }
}
