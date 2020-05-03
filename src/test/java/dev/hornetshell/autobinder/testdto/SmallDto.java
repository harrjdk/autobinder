package dev.hornetshell.autobinder.testdto;

import dev.hornetshell.autobinder.Conversion;
import dev.hornetshell.autobinder.Converter;

import java.util.List;
import java.util.UUID;

public class SmallDto {

    public static final String SMALL_DTO_USER_COUNT = "small dto user count";
    public static final String SMALL_DTO_PERMISSIONS = "small dto permissions";
    private UUID id;
    private String name;
    private String description;
    private int userCount;
    private List<String> permissions;

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

    public int getUserCount() {
        return userCount;
    }

    @Converter(value = SMALL_DTO_USER_COUNT, matchingProperty = "users")
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    @Conversion({
            @Converter(value = SMALL_DTO_PERMISSIONS, matchingProperty = "permissions")
    })
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
