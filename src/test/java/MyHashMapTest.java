import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {

    @Nested
    class ConstructorTests {
        @Test
        void testDefaultConstructor() {
            MyHashMap<String, Integer> map = new MyHashMap<>();

            assertEquals(0, map.size());
            assertTrue(map.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1})
        void testConstructorWithCapacity(int initialCapacity) {
            final float LOAD_FACTOR = 0.75F;
            MyHashMap<String, Integer> map = new MyHashMap<>(initialCapacity);

            assertEquals(0, map.size());
            assertEquals(4, map.getThreshold());
            assertTrue(map.isEmpty());
            assertEquals(LOAD_FACTOR, map.loadFactor);
        }

        @Test
        void testConstructorWithMaximumCapacity() {
            final float DEFAULT_LOAD_FACTOR = 0.75F;
            final int MAXIMUM_CAPACITY = 1 << 30;
            MyHashMap<String, Integer> map = new MyHashMap<>(-1 >>> 1);

            assertEquals(MAXIMUM_CAPACITY, map.getThreshold());
            assertTrue(map.isEmpty());
            assertEquals(DEFAULT_LOAD_FACTOR, map.loadFactor);
        }

        @Test
        void testConstructorWithCapacityAndLoadFactor() {
            final float loadFactor = 0.5f;
            MyHashMap<String, Integer> map = new MyHashMap<>(2, loadFactor);

            assertEquals(0, map.size());
            assertEquals(2, map.getThreshold());
            assertTrue(map.isEmpty());
            assertEquals(loadFactor, map.loadFactor);
        }

        @Test
        void testIllegalArguments() {
            assertThrows(IllegalArgumentException.class, () -> new MyHashMap<>(-1));
            assertThrows(IllegalArgumentException.class, () -> new MyHashMap<>(16, -0.5f));
        }
    }

    @Nested
    class ResizeTests {

        static final int MAXIMUM_CAPACITY = 1 << 30;
        static final int INITIAL_CAPACITY = 16;
        static final float LOAD_FACTOR = 0.75F;

        private MyHashMap<Integer, String> map;

        @Test
        void testResizeTriggered() {
            map = new MyHashMap<>(2, 0.75f);

            map.put(1, "one");
            map.put(2, "two");

            assertEquals(2, map.size());
            assertEquals("one", map.get(1));
            assertEquals("two", map.get(2));

            assertEquals(3, map.getThreshold());
        }

        @Test
        void testResizeTriggered2() {
            map = new MyHashMap<>(Integer.MAX_VALUE, 0.75f);

            map.put(1, "one");
            assertEquals(Integer.MAX_VALUE, map.getThreshold());
        }

        @Test
        void testResizeTriggered3() {
            map = new MyHashMap<>();

            map.setTable(new Node[1 << 30]);
            map.put(1, "1");
            assertEquals(Integer.MAX_VALUE, map.getThreshold());
        }

        @Test
        void testResizeWithCollisions() {
            map = new MyHashMap<>(10);

            map.put(16, "one");
            map.put(32, "two");
            map.put(48, "three");
            map.put(64, "four");

            map.setSize(12);
            map.put(10, "ten");
            var tab = map.getTable();

            assertEquals(map.get(32), tab[0].value);
            assertEquals(map.get(64), tab[0].next.value);

            assertEquals(map.get(16), tab[16].value);
            assertEquals(map.get(48), tab[16].next.value);

            assertEquals(map.get(10), tab[10].value);
        }
    }

    @Nested
    class GetNodeTest {
        private static MyHashMap<Integer, String> map;

        @BeforeAll
        static void setUp() {
            map = new MyHashMap<>(10);

            map.put(16, "16");
            map.put(32, "32");
        }

        @Test
        void testGetEmptyTable() {
            var map = new MyHashMap<Integer, String>();

            assertEquals(map.get(14), null);
        }

        @Test
        void testGetWithNonExistentKey() {
            assertNull(map.get(12));
        }

        @Test
        void testGetFirstInBucket() {
            assertEquals(map.get(16), "16");
        }

        @Test
        void testGetSecondBucket() {
            assertEquals(map.get(32), "32");
        }
    }

    @Nested
    class RemoveNodeTest {
        private static MyHashMap<Integer, String> map;

        @BeforeAll
        static void setUp() {
            map = new MyHashMap<>(16);

            map.put(16, "16");
            map.put(32, "32");
            map.put(48, "48");

            map.put(10, "10");
        }

        @Test
        void testRemoveEmptyTable() {
            var map = new MyHashMap<Integer, String>();

            assertNull(map.remove(1));
        }

        @Test
        void testRemoveWithNonExistentKey() {
            assertNull(map.remove(11));
        }

        @Test
        void testRemoveFirstInBucket() {
            var tab = map.getTable();

            assertEquals("16", tab[0].value);
            assertEquals(map.remove(16), "16");
            assertEquals("32", tab[0].value);
        }

        @Test
        void testRemoveNotFirstInBucket() {
            var tab = map.getTable();

            assertEquals(tab[0].next, map.getNode(32));
            assertEquals(map.remove(32), "32");
            assertEquals(tab[0].next, map.getNode(48));

        }

    }

    @Nested
    class PutValTest{
        private static MyHashMap<Integer, String> map;

        @BeforeAll
        static void setUp() {
            map = new MyHashMap<>(16);
        }

        @Test
        void testPutNewKey() {
            assertNull(map.put(1, "1"));
            assertEquals(map.get(1), "1");
        }

        @Test
        void testPutOldKey() {
            map.put(1, "1");
            assertEquals("1", map.put(1, "3"));
        }

        @Test
        void testPutWithCollisionsValue() {
            assertNull(map.put(17, "17"));
            assertEquals(map.put(17, "20"), "17");
        }
    }

    @Nested
    class PutAllTest {
        private static MyHashMap<Integer, String> mapFrom;
        private static MyHashMap<Number, String> mapToEmpty;
        private static MyHashMap<Number, String> mapToNotEmpty;


        @BeforeAll
        static void setUp() {
            mapFrom = new MyHashMap<>(1 << 15);
            for (int i = 0; i < (1 << 14); ++i) {
                mapFrom.put(i, String.valueOf(i));
            }

            mapToEmpty = new MyHashMap<>(0);

            mapToNotEmpty = new MyHashMap<>(1 << 14);
            for (int i = (1 << 13) - 1; i >= 0; --i){
                mapToNotEmpty.put(i, String.valueOf(i));
            }
        }

        @Test
        void testPutAllToEmptyMap() {
            mapToEmpty.putAll(mapFrom);

            assertEquals(1 << 14, mapToEmpty.size());
            assertEquals(mapFrom.get(16), mapToEmpty.get(16));
        }

        @Test
        void testPutAllToNotEmptyMap() {
            assertEquals(mapToNotEmpty.size(), 1 << 13);

            mapToNotEmpty.putAll(mapFrom);

            assertEquals(mapToNotEmpty.size(), 1 << 14);
        }
    }
}