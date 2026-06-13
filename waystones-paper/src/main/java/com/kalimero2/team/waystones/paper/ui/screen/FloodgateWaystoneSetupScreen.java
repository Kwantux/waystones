package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Arrays;

import static com.kalimero2.team.waystones.paper.PaperWayStones.manager;

public class FloodgateWaystoneSetupScreen implements GenericScreen {

    private final WaystoneSetupScreen setupScreen;

    protected FloodgateWaystoneSetupScreen(WaystoneSetupScreen setupScreen) {
        this.setupScreen = setupScreen;
    }


    @Override
    public void open(Player player) {
        open(player, null);
    }

    private void open(Player player, InputScreen.InputValidation lastValidation) {
        CustomForm.Builder builder = CustomForm.builder().title(TextUtil.compomentToString(setupScreen.getTitle()));

        builder.input(setupScreen.getLabel(), setupScreen.getInput().placeholder(), setupScreen.getInput().placeholder());
        if (lastValidation != null) {
            builder.label(lastValidation.message());
        }

        builder.stepSlider("Visibility", "Public", "Unlisted", "Private");
        Category[] categories = manager.getCategories().toArray(new Category[0]);
        builder.dropdown("Category", Arrays.stream(categories).map(Category::name).toList());

        builder.validResultHandler(customFormResponse -> {
            String name = customFormResponse.asInput(0);
            Visibility visibility = Visibility.valueByNumber(customFormResponse.asStepSlider(1));
            Category category = categories[customFormResponse.asDropdown(2)];
            InputScreen.InputValidation inputValidation = setupScreen.getInput().onSubmitted().apply(player, new WaystoneSetupScreen.WaystoneSetupData(name, visibility, category));

            if (inputValidation.valid()) {
                return;
            }
            open(player, inputValidation);
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }
}
