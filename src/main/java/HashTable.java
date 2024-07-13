public class HashTable {

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

        while(hashArray[hashVal] != null && hashArray[hashVal].iData != -1) {
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
