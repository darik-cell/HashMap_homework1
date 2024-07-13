public class HashTable {

    private DataItem[] hashArray;
    private int arraySize;
    private DataItem nonItem;

    public HashTable(int size) {
        arraySize = size;
        hashArray = new DataItem[arraySize];
        nonItem = new DataItem(-1);
    }

    public void displayTable() {
        System.out.println("Table: ");
        for (int i = 0; i < arraySize; i++) {
            if(hashArray[i] != null)
                System.out.print(hashArray[i].toString() + " ");
            else
                System.out.print("** ");
            if(i % 4 == 0) System.out.println();
        }
    }

    public int hashFunc(int key) { return key % arraySize; }

    //Неплохо еще проверять, чтобы массив не был заполнен, иначе цикл будет вечным
    public DataItem find(int key) {
        int hashVal = hashFunc(key);

        while(hashArray[hashVal] != null) {
            if(hashArray[hashVal].getKey() == key)
                return hashArray[hashVal];
            ++hashVal;
            hashVal %= arraySize;
        }
        return null;
    }

    public void insert(DataItem item) {
        int key = item.getKey();
        int hashVal = hashFunc(key);

        while(hashArray[hashVal] != null && hashArray[hashVal].getKey() != -1) {
            ++hashVal;
            hashVal %= arraySize;
        }
        hashArray[hashVal] = item;
    }

    public DataItem delete(int key) {
        int hashVal = hashFunc(key);

        while(hashArray[hashVal] != null) {
            if (hashArray[hashVal].getKey() == key) {
                DataItem copy = hashArray[hashVal];
                hashArray[hashVal] = nonItem;
                return copy;
            }
            ++hashVal;
            hashVal %= arraySize;
        }

        return null;
    }

}
