package space.iseki.pefile;


import org.jetbrains.annotations.NotNull;

public class ResourceNode {
    final PEFile peFile;
    final String resourceName;
    final int resourceID;
    final int irdOffset;
    final Object entry;

    ResourceNode(@NotNull PEFile peFile,
                 @NotNull String resourceName,
                 int resourceID,
                 int irdOffset,
                 @NotNull Object entry) {
        this.peFile = peFile;
        this.resourceName = resourceName;
        this.resourceID = resourceID;
        this.irdOffset = irdOffset;
        this.entry = entry;
    }

    /**
     * Returns true if the resource is a directory.
     * @return true if the resource is a directory.
     */
    public boolean isDirectory() {
        return entry instanceof ImageResourceDirectory;
    }

    /**
     * Returns true if the resource is a data entry.
     * @return true if the resource is a data entry.
     */
    public boolean isDataEntry() {
        return entry instanceof ImageResourceDataEntry;
    }

    /**
     * Returns the code page of the resource data.
     *
     * @return the code page of the resource data, unsigned. If the resource is not a data entry, returns 0.
     */
    public int getCodePage() {
        if (entry instanceof ImageResourceDataEntry) {
            return ((ImageResourceDataEntry) entry).codePage();
        } else {
            return 0;
        }
    }

    /**
     * Returns the RVA of the resource data.
     *
     * @return the RVA of the resource data, unsigned. If the resource is not a data entry, returns 0.
     */
    public int getDataRva() {
        if (entry instanceof ImageResourceDataEntry) {
            return ((ImageResourceDataEntry) entry).dataRva();
        } else {
            return 0;
        }
    }

    /**
     * Returns the size of the resource data.
     *
     * @return the size of the resource data, unsinged. If the resource is not a data entry, returns 0.
     */
    public int getSize() {
        if (entry instanceof ImageResourceDataEntry) {
            return ((ImageResourceDataEntry) entry).size();
        } else {
            return 0;
        }
    }

    /**
     * Returns the resource ID.
     * @return the resource ID, unsigned. If the resource is named, returns 0.
     */
    public int getResourceID() {
        return resourceID;
    }

    /**
     * Returns the resource name.
     * @return the resource name. If the resource is not named, returns an empty string.
     */
    public @NotNull String getResourceName() {
        return resourceName;
    }

    @Override
    public String toString() {
        var n = getResourceID() == 0 ? U.fieldEscape("resourceName", resourceName) : U.field("resourceID", resourceID);
        return U.structure("ResourceNode", n, U.field("entry", entry));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceNode that = (ResourceNode) o;
        return resourceID == that.resourceID &&
               irdOffset == that.irdOffset &&
               peFile.equals(that.peFile) &&
               resourceName.equals(that.resourceName) &&
               entry.equals(that.entry);
    }

    @Override
    public int hashCode() {
        int result = peFile.hashCode();
        result = 31 * result + resourceName.hashCode();
        result = 31 * result + resourceID;
        result = 31 * result + irdOffset;
        result = 31 * result + entry.hashCode();
        return result;
    }
}


