package ru.astravisual.module;

import net.minecraft.client.MinecraftClient;
import ru.astravisual.config.ConfigManager;

import java.util.Locale;

public abstract class Module {

    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return name.toLowerCase(Locale.ROOT).replace(" ", "");
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean value) {
        if (enabled == value) {
            return;
        }
        enabled = value;
        if (value) {
            onEnable();
        } else {
            onDisable();
        }
        ConfigManager.onModuleChanged();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void onTick(MinecraftClient client) {
    }
}
