package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.ui.util.DialogComponents;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.Visibility;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

import static com.kalimero2.team.waystones.paper.PaperWayStones.manager;
import static com.kalimero2.team.waystones.paper.util.Visibility.PRIVATE;
import static com.kalimero2.team.waystones.paper.util.Visibility.PUBLIC;
import static com.kalimero2.team.waystones.paper.util.Visibility.UNLISTED;

public class JavaWaystoneSetupScreen implements GenericScreen, Listener {

    private final PaperWayStones plugin;
    private final WaystoneSetupScreen setupScreen;

    protected JavaWaystoneSetupScreen(WaystoneSetupScreen setupScreen) {
        this.plugin = setupScreen.getPlugin();
        this.setupScreen = setupScreen;
    }


    @Override
    public void open(Player player) {
        open(player, setupScreen.getWaystone() != null ? setupScreen.getWaystone().getName() : "", "");
    }
    private void open(Player player, String initialText, String errorMessage) {

        DialogActionCallback confirmCallback = (view, audience) -> {
                    String name = view.getText("input").strip();
                    Visibility visibility = Visibility.valueOf(view.getText("visibility"));
                    Category category = manager.getCategory(view.getText("category"));
                    InputScreen.InputValidation inputValidation = setupScreen.getInput().onSubmitted().apply(player, new WaystoneSetupScreen.WaystoneSetupData(name, visibility, category));
                    if (!inputValidation.valid()) {
                        open(player, name, inputValidation.message());
                    }
                };

        DialogAction confirmAction = DialogAction.customClick(
                confirmCallback,
                ClickCallback.Options.builder()
                        .uses(100) // Set the number of uses for this callback. Defaults to 1
                        .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                        .build()
        );

        ActionButton discardButton = DialogComponents.discardButton();

        DialogType dialogType;

        if (setupScreen.getWaystone() != null) {
            ActionButton confirmButton = ActionButton.create(
                    Component.text("Save", DialogComponents.CONFIRM_COLOR),
                    Component.text("Click to confirm your input."),
                    200,
                    confirmAction
            );
            ActionButton accessSettingsButton = ActionButton.create(
                    Component.text("Manage Access", TextColor.color(0xCECB77)),
                    Component.text("Click to manage who has access to this waystone."),
                    200,
                    DialogAction.customClick(
                            (view, audience) -> {
                                confirmCallback.accept(view, audience);
                                plugin.getScreen().accessSettings((Player) audience, setupScreen.getWaystone());
                            },
                            ClickCallback.Options.builder()
                                    .uses(100) // Set the number of uses for this callback. Defaults to 1
                                    .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                                    .build()
                    )
            );
            ActionButton deleteButton = ActionButton.create(
                    Component.text("Remove Waystone", TextColor.color(0xF2404F)),
                    Component.text("Click to remove this waystone."),
                    200,
                    DialogAction.customClick(
                            (view, audience) -> {
                                plugin.getScreen().delete((Player) audience, setupScreen.getWaystone());
                            },
                            ClickCallback.Options.builder()
                                    .uses(100) // Set the number of uses for this callback. Defaults to 1
                                    .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                                    .build()
                    )
            );
            dialogType = DialogType.multiAction(List.of(confirmButton, accessSettingsButton, deleteButton)).exitAction(discardButton).columns(1).build();
        }
        else {
            ActionButton confirmButton = DialogComponents.confirmButton(confirmCallback);
            dialogType = DialogType.confirmation(confirmButton, discardButton);
        }

        Visibility defaultVisibility;
        Category defaultCategory;

        if (setupScreen.getWaystone() != null) {
            defaultVisibility = setupScreen.getWaystone().visibility();
            defaultCategory = setupScreen.getWaystone().category();
        } else {
            defaultCategory = Category.NONE;
            defaultVisibility = PUBLIC;
        }


        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(setupScreen.getTitle())
                                .inputs(List.of(
                                        DialogInput.text("input", Component.text(setupScreen.getLabel())).maxLength(setupScreen.getMaxLength()).initial(initialText).build(),
                                        DialogInput.singleOption("visibility", Component.text("Visibility"), List.of(
                                                SingleOptionDialogInput.OptionEntry.create(PUBLIC.name(), PUBLIC.text(), defaultVisibility.equals(PUBLIC)),
                                                SingleOptionDialogInput.OptionEntry.create(Visibility.UNLISTED.name(), Visibility.UNLISTED.text(), defaultVisibility.equals(UNLISTED)),
                                                SingleOptionDialogInput.OptionEntry.create(Visibility.PRIVATE.name(), Visibility.PRIVATE.text(), defaultVisibility.equals(PRIVATE))
                                        )).build(),
                                        DialogInput.singleOption("category", Component.text("Category"),
                                                manager.getCategories().stream().map(category ->
                                                    SingleOptionDialogInput.OptionEntry.create(category.name(), Component.text(category.name()), defaultCategory.equals(category))
                                                ).toList()).build()
                                        )
                                )
                                .body(List.of(
                                        DialogBody.plainMessage(Component.text(errorMessage))
                                ))
                                .canCloseWithEscape(true)
                                .build()
                )
                .type(dialogType)
        );
        player.showDialog(dialog);
    }

}
