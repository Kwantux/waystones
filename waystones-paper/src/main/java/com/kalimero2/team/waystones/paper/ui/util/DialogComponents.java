package com.kalimero2.team.waystones.paper.ui.util;

import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;

public class DialogComponents {

    public static final TextColor CONFIRM_COLOR = TextColor.color(0xAEFFC1);
    public static final TextColor DISCARD_COLOR = TextColor.color(0xFFA0B1);

    public final static ActionButton confirmButton(DialogActionCallback callback, boolean fullSize) {
        return ActionButton.create(
                Component.text("Confirm", CONFIRM_COLOR),
                Component.text("Click to confirm your input."),
                fullSize ? 200 : 100,
                DialogAction.customClick(
                        callback,
                        ClickCallback.Options.builder()
                                .uses(100) // Set the number of uses for this callback. Defaults to 1
                                .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                                .build()
                )
        );
    }

    public final static ActionButton discardButton(boolean fullSize) {
        return ActionButton.create(
                Component.text("Discard", DISCARD_COLOR),
                Component.text("Click to discard your input."),
                fullSize ? 200 : 100,
                null // If we set the action to null, it doesn't do anything and closes the dialog
        );
    }

    public final static ActionButton backButton(DialogActionCallback callback, boolean fullSize) {
        return ActionButton.create(
                Component.text("Back", DISCARD_COLOR),
                Component.text("Go back to previous screen."),
                fullSize ? 200 : 100,
                DialogAction.customClick(
                        callback,
                        ClickCallback.Options.builder()
                                .uses(100) // Set the number of uses for this callback. Defaults to 1
                                .lifetime(ClickCallback.DEFAULT_LIFETIME) // Set the lifetime of the callback. Defaults to 12 hours
                                .build()
                )
        );
    }
}
