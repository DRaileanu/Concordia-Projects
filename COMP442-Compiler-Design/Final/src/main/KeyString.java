package main;

public class KeyString {
    public final int key;
    public final String value;
    
    public KeyString(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey(){//needed for Comparator
        return key;
    }
}
