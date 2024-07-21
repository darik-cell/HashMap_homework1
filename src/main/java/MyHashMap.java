import java.util.*;
import java.util.function.Consumer;

public class MyHashMap<K, V> implements Map<K, V> {

    private Node<K, V>[] table;
    Set<Entry<K,V>> entrySet;
    Set<K> keySet;
    Collection<V> values;
    private int size;
    private int threshold;
    final float loadFactor;

    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final int INITIAL_CAPACITY = 16;
    static final float LOAD_FACTOR = 0.75F;

    public MyHashMap(int capacity, float loadFactor) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity < 0");
        }
        if (capacity > MAXIMUM_CAPACITY) capacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor");
        }
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(capacity);
    }

    public MyHashMap(int capacity) {
        this(capacity, LOAD_FACTOR);
    }

    public MyHashMap() {
        this.loadFactor = LOAD_FACTOR;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }

    final Node<K,V> getNode(Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int tabLength, hash;
        if ((tab = table) != null && (tabLength = tab.length) > 0 &&
                (first = tab[(tabLength - 1) & (hash = hash(key))]) != null) {
            if (first.hash == hash &&
                    (first.key == key || (key != null && key.equals(first.key))))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                            (e.key == key || (key != null && key.equals(e.key))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    public V put(K key, V value) {
        return putVal(hash(key), key, value);
    }

    final V putVal(int hash, K key, V value) {
        Node<K,V>[] tab; int n, i; Node<K,V> tabNode;
        if ((tab = table) == null || (n = table.length) == 0)
            n = (tab = resize()).length;
        if ((tabNode = tab[i = ((n - 1) & hash)]) == null)
            tab[i] = new Node<>(key, value, null, hash);
        else {
            Node<K,V> workNode = tabNode;
            while (!(tabNode.hash == hash &&
                    ((tabNode.key == key) || (key != null && key.equals(tabNode.key))))) {
                if ((workNode = tabNode.next) == null) break;
                tabNode = tabNode.next;
            }
            if (workNode != null) {
                V oldValue = tabNode.value;
                tabNode.value = value;
                return oldValue;
            }
            else {
                tabNode.next = new Node<>(key, value, null, hash);
            }
        }
        if (++size > threshold) resize();
        return null;
    }

    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap, oldThr;
        oldCap = (table != null) ? table.length : 0;
        oldThr = threshold;
        int newCap, newThr;
        if (oldTab == null && oldThr == 0) {
            newCap = INITIAL_CAPACITY;
            newThr = (int) (newCap * loadFactor);
        }
        else if (oldTab == null && oldThr > 0 && oldThr < MAXIMUM_CAPACITY) {
            newCap = oldThr;
            newThr = (int) (newCap * loadFactor);
        }
        else if (oldTab == null && oldThr >= MAXIMUM_CAPACITY) {
            newCap = oldThr;
            newThr = Integer.MAX_VALUE;
        }
        else if (oldTab != null) {
            if (oldCap > 0 && oldCap < 9) {
                newCap = oldCap << 1;
                newThr = (int) (newCap * loadFactor);
            }
            else if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else {
                newCap = oldCap << 1;
                newThr = oldThr << 1;
            }
        } else {
            newThr = 0;
            newCap = 0;
        }
        threshold = newThr;
        Node<K,V>[] newTab = (Node<K,V>[]) new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            Node<K,V> e;
            for (int j = 0; j < oldCap ; ++j) {
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {
                        newTab[(newCap - 1) & e.hash] = e;
                    }
                    else {
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }

                }
            }
        }
        return newTab;
    }

    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null)) == null ? null : e.value;
    }

    final Node<K,V> removeNode(int hash, Object key, Object value) {
        Node<K,V>[] tab; Node<K,V> firstNode; int index, n;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (firstNode = tab[index = ((n - 1) & hash)]) != null) {
            Node<K,V> workNode, delNode = null; K k;
            if (firstNode.hash == hash &&
                    (((k = firstNode.key) == key) || ((key != null) && key.equals(k))))
                delNode = firstNode;
            else {
                if ((workNode = firstNode.next) != null) {
                    do {
                        if (workNode.hash == hash &&
                                ((k = workNode.key) == key ||
                                        ((key != null) && key.equals(k)))) {
                            delNode = workNode;
                            break;
                        }
                        firstNode = workNode;
                    } while ((workNode = workNode.next) != null);
                }
            }
            if (delNode != null) {
                if (delNode == firstNode)
                    tab[index] = delNode.next;
                else
                    firstNode.next = delNode.next;
                return delNode;
            }
        }
        return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        Node<K,V>[] tab;
        int s = m.size();
        if ((tab = table) == null) {
            float fts = (float) s / loadFactor + 1.0F;
            int its = (fts < MAXIMUM_CAPACITY) ? (int) fts : MAXIMUM_CAPACITY;
            if (threshold < its)
                threshold = tableSizeFor(its);
        }
        else {
            while (s > threshold && table.length < MAXIMUM_CAPACITY)
                resize();
        }
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            putVal(hash(key), key, value);
        }
    }

    public void clear() {
        Node<K,V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
    }

    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }

    public boolean containsValue(Object value) {
        Node<K,V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (Node<K,V> e : tab) {
                for (; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                            (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }



    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 4 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    //Итераторы
    abstract class HashIterator {
        Node<K, V> next;
        Node<K, V> current;
        int index;

        HashIterator() {
            Node<K, V>[] tab; Node<K, V> workNode;
            next = current = null;
            index = 0;
            if ((tab = table) != null && size > 0) {
                for (; index < tab.length && (next = tab[index]) == null; ++index);
                ++index;
            }
        }

        public boolean hasNext() {
            return next != null;
        }

        final Node<K, V> nextNode() {
            Node<K, V>[] tab; Node<K, V> workNode = next; int s = 0;
            if (workNode == null) {
                throw new NoSuchElementException();
            }
            if ((next = (current = workNode).next) == null && (tab = table) != null) {
                s = tab.length;
                for (; index < s && (next = tab[index]) == null; ++index);
                ++index;
                if (index > s) index = s;
            }
            return workNode;
        }

        public void remove() {
            Node<K, V>[] tab;

            if ((tab = table) == null || tab.length <= 0 || current == null)
                throw new NoSuchElementException();
            else if (next == tab[index - 1]) {
                int i = index - 2;
                for(; tab[i] == null; i--);
                if (tab[i] == current)
                    tab[i] = null;
                else {
                    Node<K, V> workNode = tab[i];
                    while(workNode.next != current)
                        workNode = workNode.next;
                    workNode.next = null;
                }
            }
            else if (current.next == next) {
                if (current == tab[index - 1])
                    tab[index - 1] = next;
                else {
                    Node<K, V> workNode = tab[index - 1];
                    while (workNode.next != current)
                        workNode = workNode.next;
                    workNode.next = next;
                }
            }
            current = null;
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<K> {
        public K next() { return nextNode().key; }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<V> {
        public V next() { return nextNode().value; }
    }

    final class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K,V>> {
        public Map.Entry<K,V> next() { return nextNode(); }
    }

    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public final int size() { return size; }
        public final void clear() { MyHashMap.this.clear(); }
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new MyHashMap.EntryIterator();
        }

        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            Node<K,V> candidate = getNode(key);
            return candidate != null && candidate.equals(e);
        }

        public final boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value) != null;
            }
            return false;
        }
    }

    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }


    final class KeySet extends AbstractSet<K> {
        public int size() { return size; }

        public void clear() { MyHashMap.this.clear(); }

        public Iterator<K> iterator() { return new KeyIterator(); }

        public boolean contains(Object o) { return containsKey(o); }

        public boolean remove(Object key) {
            return removeNode(hash(key), key, null) != null;
        }
    }

    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    final class Values extends AbstractCollection<V> {
        public final int size() { return size; }

        public final void clear() { MyHashMap.this.clear(); }

        public final Iterator<V> iterator() { return new ValueIterator(); }

        public final boolean contains(Object o) { return containsValue(o); }
    }

    int getThreshold() {
        return threshold;
    }

    Node[] getTable() {
        return table;
    }

    void setSize(int size) {
        this.size = size;
    }

    void setTable(Node<K, V>[] tab) {
        table = tab;
    }

}






















