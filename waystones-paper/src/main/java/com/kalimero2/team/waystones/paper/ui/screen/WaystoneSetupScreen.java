package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.ui.WaystonesScreen;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class WaystoneSetupScreen implements GenericScreen {

    private final PaperWayStones plugin;
    private final JavaWaystoneSetupScreen javaScreen;
    private final GenericScreen floodgateScreen;
    private final Component title;
    private final String label;
    private final Input input;
    private final int maxLength;
    private final StoredWaystone waystone;

    public WaystoneSetupScreen(PaperWayStones plugin, Component title, String label, Input input, int maxLength, @Nullable StoredWaystone waystone) {
        this.plugin = plugin;
        this.title = title;
        this.label = label;
        this.input = input;
        this.maxLength = maxLength;
        this.waystone = waystone;
        this.javaScreen = new JavaWaystoneSetupScreen(this);
        FloodgateWaystoneSetupScreen floodgateSetupScreen = new FloodgateWaystoneSetupScreen(this);

        if (waystone != null) {
            WaystonesScreen waystonesScreen = plugin.getScreen();
            this.floodgateScreen = ButtonScreen.builder().plugin(plugin)
                    .title(title)
                    .label(Component.text(label))
                    .button(new ButtonScreen.Button(Component.text("Edit Waystone"), 0, 0), floodgateSetupScreen::open)
                    .button(new ButtonScreen.Button(Component.text("Manage Access"), 0, 0), player -> waystonesScreen.accessSettings(player, waystone))
                    .button(new ButtonScreen.Button(Component.text("Delete Waystone"), 0, 0), player -> waystonesScreen.delete(player, waystone))
                    .build();
        }
        else this.floodgateScreen = floodgateSetupScreen;
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

    protected @Nullable StoredWaystone getWaystone() {
        return waystone;
    }

    @Override
    public void open(Player player) {
        if (plugin.isBedrockPlayer(player)) {
            floodgateScreen.open(player);
        } else {
            javaScreen.open(player);
        }
    }

    public record WaystoneSetupData(String name, Visibility visibility, Category category) { }

    public record Input(String placeholder, BiFunction<Player, WaystoneSetupData, InputScreen.InputValidation> onSubmitted) { }

}
