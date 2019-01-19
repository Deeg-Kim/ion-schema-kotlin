package software.amazon.ionschema.internal.constraint

import software.amazon.ion.IonList
import software.amazon.ion.IonSymbol
import software.amazon.ion.IonValue
import software.amazon.ionschema.internal.util.Violations
import software.amazon.ionschema.internal.util.Violation

internal class Annotations(
        ion: IonValue
    ) : ConstraintBase(ion) {

    private data class Annotation(
            val text: String,
            val isRequired: Boolean
    )

    private val requiredByDefault = ion.hasTypeAnnotation("required")

    private val ordered = ion.hasTypeAnnotation("ordered")

    private val annotations = (ion as IonList).map {
            val required = if (it.hasTypeAnnotation("required")) {
                true
            } else if (it.hasTypeAnnotation("optional")) {
                false
            } else {
                requiredByDefault
            }
            Annotation((it as IonSymbol).stringValue(), required)
        }

    override fun validate(value: IonValue, issues: Violations) {
        val missingAnnotations = mutableListOf<Annotation>()
        if (ordered) {
            val valueAnnotations = value.typeAnnotations
            var valueAnnotationIndex = 0
            annotations.forEach {
                if (it.isRequired) {
                    var found = false
                    while (!found && valueAnnotationIndex < valueAnnotations.size) {
                        val valueAnnotation = valueAnnotations[valueAnnotationIndex]
                        if (it.text.equals(valueAnnotation)) {
                            found = true
                        }
                    }
                    if (!found) {
                        missingAnnotations.add(it)
                    }
                }
            }
        } else {
            annotations.forEach {
                if (it.isRequired && !value.hasTypeAnnotation(it.text)) {
                    missingAnnotations.add(it)
                }
            }
        }

        if (missingAnnotations.size > 0) {
            issues.add(Violation(ion, "missing_annotation",
                    "missing annotation(s): " + missingAnnotations.joinToString { it.text }))
        }
    }
}
