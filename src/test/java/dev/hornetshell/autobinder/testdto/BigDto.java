package dev.hornetshell.autobinder.testdto;

import java.util.List;
import java.util.UUID;


public class BigDto {

    private UUID id;
    private String name;
    private int count;
    private String description;
    private List<String> users;

    public BigDto(UUID id, String name, int count, String description, List<String> users) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.description = description;
        this.users = users;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
