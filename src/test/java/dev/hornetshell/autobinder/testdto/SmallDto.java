package dev.hornetshell.autobinder.testdto;

import java.util.UUID;

public class SmallDto {

    private UUID id;
    private String name;
    private String description;

    public SmallDto() {
        // no-op
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
