package software.amazon.ionschema.internal.util

import org.junit.Assert.assertEquals
import org.junit.Test
import software.amazon.ion.system.IonSystemBuilder
import software.amazon.ionschema.Authority
import software.amazon.ionschema.IonSchemaSystemBuilder
import software.amazon.ionschema.Type
import java.io.StringReader

class ValidatorTest {
    private val ION = IonSystemBuilder.standard().build()
    private val ISS = IonSchemaSystemBuilder.standard().addAuthority(
            object : Authority {
                override fun readerFor(id: String) = StringReader(id)
            }
    ).build()

    @Test
    fun type() {
        val type = loadType("a", "type::{ name: a, type: string }")!!
        validate(type, "3", false)
        validate(type, "hi", false)
        validate(type, "\"hi\"", true)
    }

    @Test
    fun unicode_length() {
        val type = loadType("a", "type::{ name: a, type: symbol, codepoint_length: 5 }")!!
        validate(type, "abcd", false)
        validate(type, "abcde", true)
        validate(type, "abcdef", false)
        validate(type, "\"abcd\"", false)
        validate(type, "\"abcde\"", false)
        validate(type, "\"abcdef\"", false)
    }

    @Test
    fun nested() {
        val type = loadType("a", """
            type::{
              name: a,
              type: struct,
              fields: {
                one: { type: int, occurs: required },
                two: { type: decimal, occurs: required },
                three: { type: symbol, codepoint_length: 3, occurs: required },
                four: { type: string, codepoint_length: 5, occurs: required },
                nested: {
                  type: type::{
                    type: struct,
                    fields: {
                      five: { type: list, occurs: required },
                      six: { type: sexp, occurs: required },
                      seven: { type: struct, occurs: required },
                    },
                  },
                  occurs: required,
                },
              },
            }
            """)!!
        validate(type, "{ }", false)
        validate(type, "{ one: hi }", false)
        validate(type, "{ one: 5 }", false)
        validate(type, "{ one: abc, one: hi }", false)
        validate(type, "{ one: 5, two: 5.0, three: abc, four: \"hello\", nested: { } }", false)
    }

    private fun loadType(typeName: String, schemaString: String): Type? {
        println(schemaString.replace("\n", "").replace("\r", "").replace(" ", ""))
        val schema = ISS.loadSchema(schemaString)
        return schema.getType(typeName)
    }

    private fun validate(type: Type, ionValue: String, valid: Boolean) {
        print(ionValue)
        val ion = ION.singleValue(ionValue)
        val violations = Validator.validate(type, ion)

        print(violations)
        assertEquals(valid, violations.isValid())
    }
}
