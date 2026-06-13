package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.storage.StoredWaystone;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import com.kalimero2.team.waystones.paper.ui.screen.ButtonScreen;
import com.kalimero2.team.waystones.paper.ui.screen.GenericScreen;
import com.kalimero2.team.waystones.paper.ui.screen.InputScreen;
import com.kalimero2.team.waystones.paper.ui.screen.WaystoneSetupScreen;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.Visibility;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Replace text components with translatables
public class NewScreens {

    private final PaperWayStones plugin;
    private final WaystoneManager manager;

    public NewScreens(PaperWayStones plugin) {
        this.plugin = plugin;
        this.manager = this.plugin.getManager();
    }

    /**
     * Opens the setup menu for the player
     *
     * @param player   Player that wants to set up the waystone
     * @param location Location of the waystone
     * @param stack    ItemStack that was used to create the waystone
     */
    public void setup(@NotNull Player player, @NotNull Location location, @NotNull ItemStack stack) {
        GenericScreen screen = createSetupScreen(location, stack);
        screen.open(player);
    }


    /**
     * Opens the owner change menu for the player
     *
     * @param player   Player (old Owner that wants to change the owner
     * @param waystone Waystone to change the owner
     */
    public void changeOwner(Player player, StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        InputScreen screen = createChangeOwnerScreen(waystone);
        screen.open(player);
    }

    /**
     * Opens the player add menu for the player
     *
     * @param player   Owner of the waystone
     * @param waystone Waystone
     */
    public void addPlayer(Player player, StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        InputScreen screen = createAddPlayerScreen(waystone);
        screen.open(player);
    }


    /**
     * Opens the edit menu for the player
     *
     * @param player   Player that wants to edit the waystone
     * @param waystone Waystone to edit
     */
    public void settings(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        GenericScreen screen = createSettingsScreen(waystone);
        screen.open(player);
    }


    /**
     * Opens the Waystone rename menu for the player
     *
     * @param player   The player that wants to rename the waystone
     * @param waystone The waystone to rename
     */
    public void rename(@NotNull Player player, @NotNull StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        InputScreen screen = createRenameScreen(waystone);
        screen.open(player);
    }


    /**
     * Opens a confirm screen for the player to delete the waystone
     *
     * @param player   Player that wants to delete the waystone
     * @param waystone Waystone to delete
     */
    public void delete(Player player, StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        GenericScreen screen = createDeleteScreen(waystone);
        screen.open(player);
    }


    /**
     * Opens the access settings menu for the player
     *
     * @param player   Player that wants to change the access settings
     * @param waystone Waystone to change the access settings
     */
    public void accessSettings(Player player, StoredWaystone waystone) {
        if (!manager.canEdit(waystone, player)) return;
        GenericScreen screen = createAccessSettings(waystone);
        screen.open(player);
    }

    private static int MAX_NAME_LENGTH = 16;

    private WaystoneSetupScreen createSetupScreen(@NotNull Location location, ItemStack stack) {
        return new WaystoneSetupScreen(
                plugin,
                Component.text("Waystones"),
                "Waystone Name",
                new WaystoneSetupScreen.Input("", (player, setupData) -> {

                    InputScreen.InputValidation nameValidation = validateWaystoneName(setupData.name());
                    if (nameValidation != null) return nameValidation;

                    if (!player.getGameMode().equals(GameMode.CREATIVE)) {

                        if(stack == null || stack.getAmount() < 1){
                            return new InputScreen.InputValidation(false, "You don't have any waystones in your hand!");
                        }

                        stack.setAmount(stack.getAmount() - 1);
                    }

                    StoredWaystone waystone = plugin.getManager().createWaystone(setupData.name(), player.getUniqueId(), setupData.visibility().id(), setupData.category().id(), location);
                    plugin.getDisplayManager().updateDisplay(waystone);

                    return new InputScreen.InputValidation(true, null);
                }),
                MAX_NAME_LENGTH,
                null
        );
    }

    private ButtonScreen createDeleteScreen(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).label(Component.text("Do you really want to remove this waystone?"));
        builder.plugin(plugin);
        builder.button(new ButtonScreen.Button(Component.text("Confirm"), 5, 5), player -> {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(Component.translatable("waystones.ui.remove.inventoryfull", TextUtil.RED));
                return;
            }
            if (manager.removeWaystone(waystone.id())) {
                player.sendMessage(Component.translatable("waystones.remove", TextUtil.GREEN));
                player.getInventory().addItem(plugin.getStatic());
            } else {
                player.sendMessage(Component.text("Unable to remove waystone", TextUtil.RED));
            }
            player.closeInventory();
        });
        builder.onExit((player -> settings(player, waystone)));

        return builder.build();
    }

    private InputScreen createRenameScreen(@NotNull StoredWaystone waystone) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystone " + waystone.name())).plugin(plugin);
        builder.label("Rename Waystone");
        builder.input(new InputScreen.Input(Component.text("Waystone Name"), waystone.name(), (player, input) -> {

            InputScreen.InputValidation nameValidation = validateWaystoneName(input);
            if (nameValidation != null) return nameValidation;

            if (!manager.canEdit(waystone, player)) return new InputScreen.InputValidation(false, "You don't have permission to edit this waystone!");

            boolean renamed = manager.renameWaystone(waystone.id(), input);

            if (!renamed) {
                // This shouldn't happen, because we already check the name above ...
                return new InputScreen.InputValidation(false, ":( An error occurred!");
            }

            plugin.getDisplayManager().updateDisplay(manager.getWaystone(waystone.id()));

            return new InputScreen.InputValidation(true, null);
        }));

        return builder.build();
    }

    private WaystoneSetupScreen createSettingsScreen(@NotNull StoredWaystone waystone) {
        return new WaystoneSetupScreen(
                plugin,
                Component.text("Waystone " + waystone.name()),
                "Waystone Name",
                new WaystoneSetupScreen.Input("", (player, setupData) -> {

                    if (!waystone.name().equals(setupData.name())) {
                        InputScreen.InputValidation nameValidation = validateWaystoneName(setupData.name());
                        if (nameValidation != null) return nameValidation;
                    }

                    waystone.name(setupData.name());
                    waystone.visibility(setupData.visibility());
                    waystone.category(setupData.category());

                    if (manager.canEdit(waystone, player))
                        manager.updateWaystone(waystone);

                    plugin.getDisplayManager().updateDisplay(waystone);

                    return new InputScreen.InputValidation(true, null);
                }),
                MAX_NAME_LENGTH,
                waystone
        );
    }


    private ButtonScreen createAccessSettings(@NotNull StoredWaystone waystone) {
        ButtonScreen.Builder builder = ButtonScreen.builder().title(Component.text("Waystone " + waystone.name())).label(Component.text("Access Settings"));
        builder.plugin(plugin);

        Visibility visibility = waystone.visibility();

        if (!visibility.equals(Visibility.PUBLIC)) {
            builder.button(new ButtonScreen.Button(Component.text("Add Player"), 2, 6), player -> {
                if (manager.canEdit(waystone, player))
                    plugin.getScreen().addAccess(player, waystone);
            });
            if (!manager.getAccess(waystone.id()).isEmpty())
                builder.button(new ButtonScreen.Button(Component.text("Remove Player"), 4, 7), player -> {
                    if (manager.canEdit(waystone, player))
                        plugin.getScreen().removeAccess(player, waystone);
                });
        }
        else builder.label(Component.text("This waystone is public."));
        builder.button(new ButtonScreen.Button(Component.text("Change Ownership"), 6, 80), player ->
            plugin.getScreen().transferOwnership(player, waystone)
        );

        builder.onExit((player) -> plugin.getScreen().settings(player, waystone));

        return builder.build();
    }

    private InputScreen createChangeOwnerScreen(@NotNull StoredWaystone waystone) {
        OfflinePlayer owner = plugin.getServer().getOfflinePlayer(waystone.owner());
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Waystone " + waystone.name())).label(owner.getName()).plugin(plugin);
        builder.label("Change Owner");
        builder.onExit((player) -> plugin.getScreen().accessSettings(player, waystone));
        builder.input(new InputScreen.Input(Component.text("New Owner"), "", (player, input) -> {
            if (input == null || input.isEmpty()) {
                return new InputScreen.InputValidation(false, "The name cannot be empty!");
            }
            OfflinePlayer target = plugin.getServer().getOfflinePlayerIfCached(input);
            if (target == null) {
                return new InputScreen.InputValidation(false, "This player does not exist!");
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!manager.canEdit(waystone, player)) return;
                    StoredWaystone newWaystone = new StoredWaystone(waystone.id(), waystone.name(), target.getUniqueId(), waystone.visibility(), waystone.category(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses());
                    manager.updateWaystone(newWaystone);
                    manager.addAccess(owner, waystone.id());
                    // Don't switch back to the previous screen, the player no longer has access to this waystone's settings
                }
            }.runTask(plugin);


            return new InputScreen.InputValidation(true, null);
        }));


        return builder.build();
    }

    private InputScreen createAddPlayerScreen(@NotNull StoredWaystone waystone) {
        InputScreen.Builder builder = InputScreen.builder().title(Component.text("Add Access")).plugin(plugin);
        builder.label("Add a Player");
        builder.onExit(player -> accessSettings(player, waystone));
        builder.input(new InputScreen.Input(Component.text("New Player"), "", (player, input) -> {
            if (input == null || input.isEmpty()) {
                return new InputScreen.InputValidation(false, "The name cannot be empty!");
            }
            OfflinePlayer target = plugin.getServer().getOfflinePlayerIfCached(input);
            if (target == null) {
                return new InputScreen.InputValidation(false, "This player does not exist!");
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (manager.canEdit(waystone, player))
                        manager.addAccess(target, waystone.id());
                    accessSettings(player, waystone);
                }
            }.runTask(plugin);

            return new InputScreen.InputValidation(true, null);
        }));


        return builder.build();
    }

    @Nullable
    private InputScreen.InputValidation validateWaystoneName(String input) {
        if (input == null || input.isEmpty()) {
            return new InputScreen.InputValidation(false, "The name cannot be empty!");
        }

        if (input.length() > MAX_NAME_LENGTH) {
            return new InputScreen.InputValidation(false, "The name cannot be longer than 16 characters!");
        }

        if (manager.isNameUsed(input)) {
            return new InputScreen.InputValidation(false, "This name is already in use!");
        }
        return null;
    }

}
