package stepDefinitions;

import framework.Element;
import org.openqa.selenium.NoSuchElementException;

import java.util.ArrayList;
import java.util.List;

public class SavedObjects {
    List<String> strings = new ArrayList<>();
    List<Double> doubles = new ArrayList<>();
    List<Integer> integers = new ArrayList<>();
    List<Element> elements = new ArrayList<>();
    List<List<String>> lists = new ArrayList<>();

    String values;


    public SavedObjects(String value) {
        this.strings.add(value);
        values = "String";
    }

    SavedObjects(Double value) {
        this.doubles.add(value);
        values = "Double";
    }

    SavedObjects(Integer value) {
        this.integers.add(Integer.valueOf(value));
        values = "Integer";
    }

    SavedObjects(Element value) {
        this.elements.add(value);
        values = "Element";
    }

    public SavedObjects(ArrayList<String> lists, boolean isList) {
        this.lists.add(lists);
        values = "Lists";
    }


    public SavedObjects(List<Object> value) {
        if (value.get(0) instanceof String) saveStrings(value);
        else if (value.get(0) instanceof Double) saveDoubles(value);
        else if (value.get(0) instanceof Integer) saveIntegers(value);
        else throw new NoSuchElementException("Unknown object type. It's not String or Double or Integers");
    }

    SavedObjects(List<Element> value, boolean leaveBlankIfNotElement) {
        this.elements = value;
        values = "Element List";
    }

    private void saveDoubles(List<Object> doubles) {
        for (Object object : doubles) {
            this.doubles.add(Double.valueOf(object.toString()));
        }
        values = "Double List";
    }

    private void saveIntegers(List<Object> integers) {
        for (Object object : integers) {
            this.integers.add(Integer.valueOf(object.toString()));
        }
        values = "Integer List";
    }

    private void saveStrings(List<Object> strings) {
        for (Object object : strings) {
            this.strings.add(object.toString());
        }
        values = "String List";
    }

    public <Any> Any returnSaved() {
        switch (values) {
            case "String":
                return ((Any) strings.get(0));
            case "String List":
                return ((Any) strings);
            case "Double":
                return ((Any) doubles.get(0));
            case "Double List":
                return ((Any) doubles);
            case "Integer":
                return ((Any) integers.get(0));
            case "Integer List":
                return ((Any) integers);
            case "Element":
                return ((Any) elements.get(0));
            case "Element List": {
                return ((Any) elements);
            }
            case "Lists": {
                return ((Any) lists.get(0));
            }
        }
        return null;
    }
}

