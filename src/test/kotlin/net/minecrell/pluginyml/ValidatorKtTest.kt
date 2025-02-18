package net.minecrell.pluginyml

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ValidatorKtTest {

    @Test
    fun `assertApiVersion should pass for valid api version matching regex`() {
        val apiVersion = "1.21"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertApiVersion(apiVersion, validRegex, minApiVersion)
    }

    @Test
    fun `assertApiVersion should throw when api version does not match regex`() {
        val apiVersion = "invalid.version"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion(apiVersion, validRegex, minApiVersion)
        }
    }

    @Test
    fun `assertApiVersion should throw when api version is less than minimum in two-part version`() {
        val apiVersion = "1.19"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion(apiVersion, validRegex, minApiVersion)
        }
    }

    @Test
    fun `assertApiVersion should throw when second segment is less than 20 in three-part version`() {
        val apiVersion = "1.19.4"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion(apiVersion, validRegex, minApiVersion)
        }
    }

    @Test
    fun `assertApiVersion should throw when second segment is 20 and third segment is less than 5`() {
        val apiVersion = "1.20.4"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion(apiVersion, validRegex, minApiVersion)
        }
    }

    @Test
    fun `assertApiVersion should pass for valid three-part api version 1_20_5 or higher`() {
        val apiVersion = "1.20.5"
        val validRegex = Regex("^\\d+\\.\\d+(\\.\\d+)?$")
        val minApiVersion = 20

        assertApiVersion(apiVersion, validRegex, minApiVersion)
    }

    @Test
    fun `assert some general versions`() {
        val validRegex = Regex("^1\\.[1-9][0-9]*(\\.[1-9][0-9]*)?$")
        val minApiVersion = 13

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.012", validRegex, minApiVersion)
        }

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.12", validRegex, minApiVersion)
        }

        assertApiVersion("1.13", validRegex, minApiVersion) // Should pass
        assertApiVersion("1.19", validRegex, minApiVersion) // Should pass
        assertApiVersion("1.20", validRegex, minApiVersion) // Should pass

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.20.3", validRegex, minApiVersion)
        }

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.20.4", validRegex, minApiVersion)
        }

        assertApiVersion("1.20.5", validRegex, minApiVersion) // Should pass
        assertApiVersion("1.20.6", validRegex, minApiVersion) // Should pass
        assertApiVersion("1.21", validRegex, minApiVersion) // Should pass

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.021", validRegex, minApiVersion)
        }

        assertApiVersion("1.21.1", validRegex, minApiVersion) // Should pass

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.21.01", validRegex, minApiVersion)
        }

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("1.21.1.", validRegex, minApiVersion)
        }

        assertFailsWith<InvalidPluginDescriptionException> {
            assertApiVersion("2.1", validRegex, minApiVersion)
        }
    }
}
