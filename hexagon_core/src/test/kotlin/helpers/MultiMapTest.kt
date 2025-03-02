package com.hexagonkt.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MultiMapTest {

    @Test fun `MultiMap of nullable types`() {
        val multiMap: MultiMap<String, String?> = multiMapOf("a" to listOf("b", "c", null))

        assertEquals("b", multiMap["a"])
        assertEquals(listOf("b", "c", null), multiMap.allValues["a"])
        assertNull(multiMap["b"])

        assertEquals(mapOf("a" to "b").entries, multiMap.entries)
        assertEquals(setOf("a"), multiMap.keys)
        assertEquals(1, multiMap.size)
        assertEquals(listOf("b"), multiMap.values)
        assertTrue(multiMap.containsKey("a"))
        assertTrue(multiMap.containsValue("b"))
        assertTrue(multiMap.containsValue("c"))
        assertTrue(multiMap.containsValue(null))
        assertFalse(multiMap.isEmpty())
    }

    @Test fun `MultiMaps are comparable`() {
        val multiMap1 = multiMapOf("a" to listOf("b", "c"))
        val mapData = mapOf("a" to listOf("b", "c"))
        val multiMap2 = MultiMap(mapData)
        assertEquals(multiMap1, multiMap2)
        assertTrue(multiMap1 == mapData)
        assertTrue(multiMap2 == mapData)
        assertEquals(multiMap1.hashCode(), multiMap2.hashCode())
    }

    @Test fun `All MultiMap values can be accessed`() {
        val multiMap: MultiMap<String, String> = multiMapOf(
            "a" to listOf("b", "c"),
            "b" to listOf("d", "e"),
            "c" to emptyList()
        )

        assertEquals("b", multiMap["a"])
        assertEquals("d", multiMap["b"])
        assertEquals(listOf("b", "c"), multiMap.allValues["a"])
        assertEquals(listOf("d", "e"), multiMap.allValues["b"])
        assertNull(multiMap["c"])

        assertEquals(mapOf("a" to "b", "b" to "d").entries, multiMap.entries)
        assertEquals(setOf("a", "b"), multiMap.keys)
        assertEquals(2, multiMap.size)
        assertEquals(listOf("b", "d"), multiMap.values)
        assertTrue(multiMap.containsKey("a"))
        assertTrue(multiMap.containsKey("b"))
        assertTrue(multiMap.containsValue("b"))
        assertTrue(multiMap.containsValue("c"))
        assertTrue(multiMap.containsValue("d"))
        assertTrue(multiMap.containsValue("e"))
        assertFalse(multiMap.isEmpty())
    }

    @Test fun `Empty lists are filtered out`() {
        val multiMap: MultiMap<String, String?> = mapOf("a" to emptyList<String>()).toMultiMap()

        assertNull(multiMap["a"])
        assertNull(multiMap.allValues["a"])
        assertNull(multiMap["b"])

        assertEquals(emptySet(), multiMap.entries)
        assertEquals(emptySet(), multiMap.keys)
        assertEquals(0, multiMap.size)
        assertEquals(emptyList(), multiMap.values)
        assertFalse(multiMap.containsKey("a"))
        assertFalse(multiMap.containsValue("b"))
        assertFalse(multiMap.containsValue("c"))
        assertFalse(multiMap.containsValue(null))
        assertTrue(multiMap.isEmpty())
    }
}
