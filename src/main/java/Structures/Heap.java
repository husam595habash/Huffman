package Structures;

public class Heap {
    private int size;
    private int capacity;
    private BTNode arr[];

    public Heap() {}

    public Heap(int size, BTNode array[], int capacity) {
        arr = array;
        this.size = size;
        this.capacity = capacity;
        buildHeap(array);
    }

    public int getSize() {
        return size;
    }


    public BTNode[] getArr() {
        return arr;
    }


    public void insert(BTNode x) {
        if (isFull()) {
            System.out.println("the heap is full");
            return;
        }
        int i = (++capacity);

        while (i > 1 && x.getfrequency() < arr[i / 2].getfrequency()) {
            arr[i] = arr[i / 2];
            i /= 2;
        }
        arr[i] = x;

    }

    public void buildHeap(BTNode ar[]) {
        int starting = capacity / 2;
        for (int i = starting; i >= 1; i--) {
            heapify(i);
        }
    }

    public void heapify(int i) {
        int min = i;

        if ((i * 2) <= capacity && arr[min].getfrequency() > arr[i * 2].getfrequency()) {
            min = i * 2;
        }
        if ((i * 2 + 1) <= capacity && arr[min].getfrequency() > arr[i * 2 + 1].getfrequency()) {
            min = i * 2 + 1;
        }

        if (arr[min] != arr[i]) {
            BTNode temp = arr[min];
            arr[min] = arr[i];
            arr[i] = temp;
            heapify(min);
        }

    }

    public BTNode extractMin() {
        int i, child;
        BTNode min, last;
        if (isEmpty()) {
            System.out.println("it's empty");
            return null;
        }

        min = this.arr[1];
        last = this.arr[capacity--];
        for (i = 1; i * 2 <= capacity; i = child) {
            child = i * 2;
            if ((child != capacity) && this.arr[child + 1].getfrequency() < arr[child].getfrequency())
                child++;
            if (last.getfrequency() > arr[child].getfrequency()) {
                arr[i] = arr[child];
            } else {
                break;
            }
        }
        arr[i] = last;
        return min;

    }

    public boolean isEmpty() {
        if (capacity == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFull() {
        if (capacity + 1 == size) {
            return true;
        } else {
            return false;
        }
    }



}
