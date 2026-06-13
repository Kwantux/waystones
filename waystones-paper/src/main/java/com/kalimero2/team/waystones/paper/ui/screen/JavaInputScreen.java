package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.ui.util.DialogComponents;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class JavaInputScreen implements GenericScreen, Listener {

    private final PaperWayStones plugin;
    private final InputScreen inputScreen;

    protected JavaInputScreen(InputScreen inputScreen) {
        this.plugin = inputScreen.getPlugin();
        this.inputScreen = inputScreen;
    }


    @Override
    public void open(Player player) {
        open(player, "", "");
    }
    private void open(Player player, String initialText, String errorMessage) {

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(inputScreen.getTitle())
                                .inputs(List.of(
                                        DialogInput.text("input", Component.text(inputScreen.getLabel())).maxLength(inputScreen.getMaxLength()).initial(initialText).build()
                                ))
                                .body(List.of(
                                        DialogBody.plainMessage(Component.text(errorMessage))
                                ))
                                .canCloseWithEscape(true)
                                .build()
                )
                .type(DialogType.confirmation(
                        DialogComponents.confirmButton(
                            (view, audience) -> {
                                        String text = view.getText("input").strip();
                                        InputScreen.InputValidation inputValidation = inputScreen.getInput().onSubmitted().apply(player, text);
                                        if (!inputValidation.valid()) {
                                            open(player, text, inputValidation.message());
                                        }
                            }, false
                        ),
                        inputScreen.hasOnExit() ?
                                DialogComponents.backButton((view, audience) -> {
                                    inputScreen.callOnExit(player);
                                }, false) :
                                DialogComponents.discardButton(false)

                ))
        );
        player.showDialog(dialog);
    }

}
