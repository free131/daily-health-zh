package com.apoorvdarshan.calorietracker.data

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import org.junit.Assert.*
import org.junit.Test

class ChineseResourceCoverageTest {
    private fun resources(folder: String): Map<String, Element> = buildMap {
        File("src/main/res/$folder").listFiles()!!.filter { it.extension == "xml" }.forEach { file ->
            val factory = DocumentBuilderFactory.newInstance()
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            val nodes = factory.newDocumentBuilder().parse(file).documentElement.childNodes
            for (i in 0 until nodes.length) {
                val element = nodes.item(i) as? Element ?: continue
                if (element.tagName !in setOf("string", "plurals", "string-array")) continue
                val name = element.getAttribute("name")
                assertFalse("Duplicate $folder/$name", containsKey(name))
                put(name, element)
            }
        }
    }

    private fun placeholders(element: Element): List<String> {
        val content = if (element.tagName == "plurals") {
            val items = element.getElementsByTagName("item")
            (0 until items.length).map { items.item(it) as Element }
                .first { it.getAttribute("quantity") == "other" }.textContent
        } else element.textContent
        return Regex("%(?:\\d+\\$)?[dsf]").findAll(content).map { it.value }.sorted().toList()
    }

    @Test fun simplifiedChineseHasAllFallbacksWithCompatibleFormatArguments() {
        val english = resources("values")
        val chinese = resources("values-zh-rCN")
        english.forEach { (name, element) ->
            if (element.getAttribute("translatable") == "false" && name !in chinese) return@forEach
            val localized = chinese[name]
            assertNotNull("Missing Chinese: $name", localized)
            assertEquals(name, element.tagName, localized!!.tagName)
            assertEquals(name, placeholders(element), placeholders(localized))
        }
    }
}
