package main;
// Wrapper class used to somewhat simulate pointers, since Java doesn't pass by reference

public class Pointer<T> {
    public T ref;

    public Pointer(){
        ref = null;
    }
    public Pointer(T ref){
        this.ref = ref;
    }

    public void ref(T ref){
        this.ref = ref;
    }
    public T deref(){
        return ref;
    }

}
