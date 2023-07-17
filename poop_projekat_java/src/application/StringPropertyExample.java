package application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringPropertyExample {
    public static void main(String[] args) {
        StringProperty stringProperty = new SimpleStringProperty("Hello");

        // Adding a listener to the StringProperty
        stringProperty.addListener((observable, oldValue, newValue) ->
                System.out.println("String value changed: " + newValue));
        

        // Modifying the value of the StringProperty
        stringProperty.set("World");

        // Accessing the value of the StringProperty
        String value = stringProperty.get();
        System.out.println("String value: " + value);
    }
}