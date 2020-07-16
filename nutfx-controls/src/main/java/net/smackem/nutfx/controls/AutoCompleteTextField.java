package net.smackem.nutfx.controls;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a TextField which implements an "autocomplete" functionality,
 * based on a supplied list of entries.<p>
 * <p>
 * If the entered text matches a part of any of the supplied entries these are
 * going to be displayed in a popup. Further the matching part of the entry is
 * going to be displayed in a special style, defined by
 * {@link #textOccurrenceStyle textOccurenceStyle}. The maximum number of
 * displayed entries in the popup is defined by
 * {@link #maxEntries maxEntries}.<br>
 * By default the pattern matching is not case-sensitive. This behaviour is
 * defined by the {@link #caseSensitive caseSensitive}
 * .<p>
 * <p>
 * The AutoCompleteTextField also has a List of
 * {@link #filteredEntries filteredEntries} that is equal to the search results
 * if search results are not empty, or {@link #filteredEntries filteredEntries}
 * is equal to {@link #entries entries} otherwise. If
 * {@link #popupHidden popupHidden} is set to true no popup is going to be
 * shown. This list can be used to bind all entries to another node (a ListView
 * for example) in the following way:
 * <pre>
 * <code>
 * AutoCompleteTextField auto = new AutoCompleteTextField(entries);
 * auto.setPopupHidden(true);
 * SimpleListProperty filteredEntries = new SimpleListProperty(auto.getFilteredEntries());
 * listView.itemsProperty().bind(filteredEntries);
 * </code>
 * </pre>
 *
 * @param <S>
 * @author Caleb Brinkman
 * @author Fabian Ochmann
 * @author smackem
 */
public class AutoCompleteTextField<S extends Comparable<S>> extends TextField {

    private final ObjectProperty<S> lastSelectedItem = new SimpleObjectProperty<>();

    /**
     * The existing autocomplete entries.
     */
    private final SortedSet<S> entries = new TreeSet<>();

    /**
     * The set of filtered entries:<br>
     * Equal to the search results if search results are not empty, equal to
     * {@link #entries entries} otherwise.
     */
    private final ObservableList<S> filteredEntries = FXCollections.observableArrayList();

    /**
     * The popup used to select an entry.
     */
    private final ContextMenu entriesPopup;

    /**
     * Indicates whether the search is case sensitive or not. <br>
     * Default: false
     */
    private boolean caseSensitive;

    /**
     * Indicates whether the Popup should be hidden or displayed. Use this if
     * you want to filter an existing list/set (for example values of a
     * {@link javafx.scene.control.ListView ListView}). Do this by binding
     * {@link #getFilteredEntries() getFilteredEntries()} to the list/set.
     */
    private boolean popupHidden;

    /**
     * The CSS style that should be applied on the parts in the popup that match
     * the entered text. <br>
     * Default: "-fx-font-weight: bold; -fx-fill: red;"
     * <p>
     * Note: This style is going to be applied on an
     * {@link javafx.scene.text.Text Text} instance. See the <i>JavaFX CSS
     * Reference Guide</i> for available CSS Propeties.
     */
    private String textOccurrenceStyle = """
                                         -fx-font-weight: bold;
                                         -fx-fill: red;
                                         """;

    /**
     * The maximum Number of entries displayed in the popup.<br>
     * Default: 10
     */
    private int maxEntries = 10;

    /**
     * Construct a new AutoCompleteTextField.
     *
     * @param entrySet the entries to contain
     */
    public AutoCompleteTextField(Collection<S> entrySet) {
        if (entrySet != null) {
            this.entries.addAll(entrySet);
        }
        this.filteredEntries.addAll(this.entries);
        this.entriesPopup = new ContextMenu();
        textProperty().addListener(this::onTextChanged);
        focusedProperty().addListener(this::onFocusChanged);
    }

    public AutoCompleteTextField() {
        this(null);
    }

    private void onTextChanged(ObservableValue<? extends String> prop, String old, String val) {
        if (val == null || val.length() == 0) {
            this.filteredEntries.clear();
            this.filteredEntries.addAll(this.entries);
            this.entriesPopup.hide();
        } else {
            final LinkedList<S> searchResult = new LinkedList<>();
            //Check if the entered Text is part of some entry
            final Pattern pattern = Pattern.compile(".*" + val + ".*",
                    isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE);
            for (final S entry : this.entries) {
                final Matcher matcher = pattern.matcher(entry.toString());
                if (matcher.matches()) {
                    searchResult.add(entry);
                }
            }
            if (this.entries.isEmpty() == false) {
                this.filteredEntries.clear();
                this.filteredEntries.addAll(searchResult);
                //Only show popup if not in filter mode
                if (isPopupHidden() == false) {
                    populatePopup(searchResult, val);
                    if (this.entriesPopup.isShowing() == false) {
                        this.entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                    }
                }
            } else {
                this.entriesPopup.hide();
            }
        }
    }

    private void onFocusChanged(Observable observable) {
        this.entriesPopup.hide();
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<S> getEntries() {
        return this.entries;
    }

    private void populatePopup(List<S> searchResult, String text) {
        final List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), getMaxEntries());
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i).toString();
            final S itemObject = searchResult.get(i);

            final int occurrence;
            if (isCaseSensitive()) {
                occurrence = result.indexOf(text);
            } else {
                occurrence = result.toLowerCase().indexOf(text.toLowerCase());
            }
            if (occurrence < 0) {
                continue;
            }
            //Part before occurrence (might be empty)
            final Text pre = new Text(result.substring(0, occurrence));
            //Part of (first) occurrence
            final Text in = new Text(result.substring(occurrence, occurrence + text.length()));
            in.setStyle(getTextOccurrenceStyle());
            //Part after occurrence
            final Text post = new Text(result.substring(occurrence + text.length(), result.length()));
            final TextFlow entryFlow = new TextFlow(pre, in, post);
            final CustomMenuItem item = new CustomMenuItem(entryFlow, true);
            item.setOnAction(e -> {
                this.lastSelectedItem.set(itemObject);
                this.entriesPopup.hide();
            });
            menuItems.add(item);
        }
        this.entriesPopup.getItems().clear();
        this.entriesPopup.getItems().addAll(menuItems);
    }

    public S getLastSelectedObject() {
        return this.lastSelectedItem.get();
    }

    public ContextMenu getEntryMenu() {
        return this.entriesPopup;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public String getTextOccurrenceStyle() {
        return this.textOccurrenceStyle;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setTextOccurrenceStyle(String textOccurrenceStyle) {
        this.textOccurrenceStyle = textOccurrenceStyle;
    }

    public boolean isPopupHidden() {
        return this.popupHidden;
    }

    public void setPopupHidden(boolean popupHidden) {
        this.popupHidden = popupHidden;
    }

    public ObservableList<S> getFilteredEntries() {
        return this.filteredEntries;
    }

    public int getMaxEntries() {
        return this.maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }
}