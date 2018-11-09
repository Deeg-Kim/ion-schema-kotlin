package software.amazon.ionschema

import software.amazon.ion.IonValue

interface Type {
    fun name(): String
    fun isValid(value: IonValue): Boolean
}
