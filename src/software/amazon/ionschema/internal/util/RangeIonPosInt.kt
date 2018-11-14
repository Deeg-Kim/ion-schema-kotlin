package software.amazon.ionschema.internal.util

import software.amazon.ion.IonInt
import software.amazon.ion.IonList
import software.amazon.ion.IonValue
import software.amazon.ionschema.InvalidSchemaException
import java.math.BigDecimal

internal class RangeIonPosInt(ion: IonList) : Range {
    private val delegate = RangeIonNumber(ion)

    init {
        if (!((ion[0] is IonInt && (ion[0] as IonInt).intValue() >= 0)
                        || ion[0].equals(Range.MIN))) {
            throw InvalidSchemaException("Invalid min boundary in positive int $ion")
        }

        if (!((ion[1] is IonInt && (ion[1] as IonInt).intValue() >= 0)
                        || ion[1].equals(Range.MAX))) {
            throw InvalidSchemaException("Invalid max boundary in positive int $ion")
        }

        if (delegate.min.value != null && delegate.max.value != null
                && (delegate.min.boundaryType == RangeBoundaryType.EXCLUSIVE
                    || delegate.max.boundaryType == RangeBoundaryType.EXCLUSIVE)) {
            val minPlusOne = delegate.min.value.add(BigDecimal.ONE)
            if (minPlusOne.equals(delegate.max.value)) {
                throw InvalidSchemaException("No valid values in the positive int range $ion")
            }
        }

        if ((delegate.min.value != null && delegate.min.value.compareTo(BigDecimal.ZERO) < 0)
                || (delegate.max.value != null && delegate.max.value.compareTo(BigDecimal.ZERO) < 0)) {
            throw InvalidSchemaException("Invalid positive integer range $ion")
        }
    }

    override fun contains(value: Int) = delegate.contains(value)
    override fun contains(value: IonValue) = delegate.contains(value)
    override fun compareTo(value: Int) = delegate.compareTo(value)
}
