package javafinalproject;

import java.util.HashSet;
import java.util.Set;

public class Player extends Entity {
    private Set<String> inventory;

    public Player(Position position) {
        super(position);
        this.inventory = new HashSet<>();
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    public void addKey(String keyId) {
        inventory.add(keyId);
    }

    public boolean hasKey(String keyId) {
        return inventory.contains(keyId);
    }

    public Set<String> getInventory() {
        return inventory;
    }
}