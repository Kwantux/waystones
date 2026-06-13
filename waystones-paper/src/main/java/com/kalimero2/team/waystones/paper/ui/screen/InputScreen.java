package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class InputScreen implements GenericScreen {

    private final PaperWayStones plugin;
    private final JavaInputScreen javaScreen;
    private final FloodgateInputScreen floodgateScreen;
    private final Component title;
    private final String label;
    private final Input input;
    private final int maxLength;
    private final Consumer<Player> onExit;

    private InputScreen(PaperWayStones plugin, Component title, String label, Input input, int maxLength, Consumer<Player> onExit) {
        this.plugin = plugin;
        this.title = title;
        this.label = label;
        this.input = input;
        this.maxLength = maxLength;
        this.onExit = onExit;
        this.javaScreen = new JavaInputScreen(this);
        this.floodgateScreen = new FloodgateInputScreen(this);
    }

    public static InputScreen.Builder builder() {
        return new InputScreen.Builder();
    }

    protected PaperWayStones getPlugin() {
        return plugin;
    }

    protected Component getTitle() {
        return title;
    }

    protected String getLabel() {
        return label;
    }

    protected Input getInput() {
        return input;
    }

    protected int getMaxLength() {
        return maxLength;
    }

    protected boolean hasOnExit() {
        return onExit != null;
    }

    protected void callOnExit(Player player) {
        if (hasOnExit()) {
            onExit.accept(player);
        }
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
        private PaperWayStones plugin;
        private Component title;
        private String label;
        private Input input;
        private int maxLength = 20;
        private Consumer<Player> onExit = null;

        public Builder plugin(PaperWayStones plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder title(Component title) {
            this.title = title;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder input(Input input) {
            this.input = input;
            return this;
        }

        /**
         * Sets the maximum length for inputs (only applicable for Java players).
         */
        public Builder maxLength(int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder onExit(Consumer<Player> onExit) {
            this.onExit = onExit;
            return this;
        }

        public InputScreen build() {
            return new InputScreen(plugin, title, label, input, maxLength, onExit);
        }
    }

    public record Input(Component title, String placeholder, ItemStack itemLeft, ItemStack itemResult,
                        BiFunction<Player, String, InputValidation> onSubmitted) {
        public Input(Component title, String placeholder, BiFunction<Player, String, InputValidation> onSubmitted) {
            this(title, placeholder, null, null, onSubmitted);
        }
    }

    public record InputValidation(boolean valid, String message) {
    }

}
