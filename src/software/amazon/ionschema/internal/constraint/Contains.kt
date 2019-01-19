package software.amazon.ionschema.internal.constraint

import software.amazon.ion.IonContainer
import software.amazon.ion.IonList
import software.amazon.ion.IonValue
import software.amazon.ionschema.internal.util.Violations
import software.amazon.ionschema.internal.util.Violation
import software.amazon.ionschema.internal.util.CommonViolations

internal class Contains(
        ion: IonValue
    ) : ConstraintBase(ion) {

    private val expectedElements = (ion as IonList).toArray()

    override fun validate(value: IonValue, issues: Violations) {
        if (value !is IonContainer) {
            issues.add(CommonViolations.INVALID_TYPE(ion, value))
        } else if (value.isNullValue) {
            issues.add(CommonViolations.NULL_VALUE(ion))
        } else {
            val expectedValues = expectedElements.toMutableSet()
            value.forEach {
                expectedValues.remove(it)
            }
            if (!expectedValues.isEmpty()) {
                issues.add(Violation(ion, "missing_values",
                        "missing value(s): " + expectedValues.joinToString { it.toString() }))
            }
        }
    }
}
