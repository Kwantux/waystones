package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.incendo.cloud.exception.handling.ExceptionHandlerRegistration;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ButtonScreen implements GenericScreen {

    private final PaperWayStones plugin;
    private final JavaButtonScreen javaScreen;
    private final FloodgateButtonScreen floodgateScreen;
    private final Component title;
    private final Component label;
    private final HashMap<Button, Consumer<Player>> buttons;
    private final Consumer<Player> onExit;

    private ButtonScreen(PaperWayStones plugin, Component title, Component label, HashMap<Button, Consumer<Player>> buttons, Consumer<Player> onExit) {
        this.plugin = plugin;
        this.title = title;
        this.label = label;
        this.buttons = buttons;
        this.onExit = onExit;
        this.javaScreen = new JavaButtonScreen(this);
        this.floodgateScreen = new FloodgateButtonScreen(this);
    }

    public static ButtonScreen.Builder builder() {
        return new ButtonScreen.Builder();
    }

    protected PaperWayStones getPlugin() {
        return plugin;
    }

    protected Component getTitle() {
        return title;
    }

    protected Component getLabel() {
        return label;
    }

    protected HashMap<Button, Consumer<Player>> getButtons() {
        return buttons;
    }

    protected void onExit(Player player) {
        onExit.accept(player);
    }

    @Override
    public void open(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreen.open(player);
        } else {
            javaScreen.open(player);
        }
    }

    public static class Builder {
        private final HashMap<ButtonScreen.Button, Consumer<Player>> buttons = new HashMap<>();
        private PaperWayStones plugin;
        private Component title;
        private Component label;
        private Consumer<Player> onExit = (player) -> {};

        public Builder plugin(PaperWayStones plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder label(Component label) {
            this.label = label;
            return this;
        }

        public Builder button(Button button, Consumer<Player> action) {
            buttons.put(button, action);
            return this;
        }

        public Builder onExit(Consumer<Player> onExit) {
            this.onExit = onExit;
            return this;
        }

        public ButtonScreen build() {
            return new ButtonScreen(plugin, title, label, buttons, onExit);
        }
    }

    public record Button(Component name, int slot, int modelData, Material material) {

        public Button(Component name, int slot) {
            this(name, slot, 0);
        }

        public Button(Component name, int slot, int modelData) {
            this(name, slot, modelData, Material.PAPER);
        }

    }

}
