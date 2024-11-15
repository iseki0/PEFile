package space.iseki.pefile;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class ImportSymbol {
    private final @NotNull @NonNls String name;
    private final int ordinal;

    ImportSymbol(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    /**
     * Returns the ordinal of the symbol.
     * @return the ordinal of the symbol. If the symbol is imported by name, returns 0.
     */
    public int getOrdinal() {
        return ordinal;
    }

    /**
     * Returns the name of the symbol.
     * @return the name of the symbol. If the symbol is imported by ordinal, returns the ordinal as a string.
     */
    public @NotNull @NonNls String getName() {
        if (name.isEmpty() && ordinal != 0) {
            return Integer.toString(ordinal);
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportSymbol that = (ImportSymbol) o;
        return ordinal == that.ordinal && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ordinal;
        return result;
    }

    @Override
    public String toString() {
        return U.structure("ImportSymbol", ordinal == 0 ? U.fieldEscape("name", name) : U.field("ordinal", ordinal));
    }
}
