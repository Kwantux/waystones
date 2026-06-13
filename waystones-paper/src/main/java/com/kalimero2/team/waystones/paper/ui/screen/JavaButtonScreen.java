package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.ui.util.DialogComponents;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public class JavaButtonScreen implements GenericScreen, Listener {

    private final Dialog dialog;

    protected JavaButtonScreen(ButtonScreen buttonScreen) {
        ActionButton backButton = DialogComponents.backButton((view, audience) -> buttonScreen.onExit((Player) audience), true);
        dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(buttonScreen.getTitle()).body(List.of(DialogBody.plainMessage(buttonScreen.getLabel()))).build())
                .type(DialogType.multiAction(
                    buttonScreen.getButtons().entrySet().stream().map(entry ->
                        ActionButton.create(entry.getKey().name(), null, 200, DialogAction.staticAction(ClickEvent.callback(audience -> entry.getValue().accept((Player) audience))))
                    ).toList()
                ).columns(1).exitAction(backButton).build())
        );
    }

    @Override
    public void open(Player player) {
        player.showDialog(dialog);
    }
}
