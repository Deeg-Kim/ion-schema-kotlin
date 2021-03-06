package software.amazon.ionschema.internal.util

import software.amazon.ion.IonValue

internal fun IonValue.withoutTypeAnnotations() =
        if (this.typeAnnotations.size > 0) {
            val v = this.clone()
            v.clearTypeAnnotations()
            v
        } else {
            this
        }
