package de.jmizv.jiexplorer.util;

import java.util.UUID;

public final class UID {

    private final UUID uuid;

    public UID() {
        this.uuid = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "uid" + this.uuid.toString().replaceAll(":", "f").replaceAll("-", "e").replaceAll("'", "a");
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return this.uuid.toString();
    }
}
