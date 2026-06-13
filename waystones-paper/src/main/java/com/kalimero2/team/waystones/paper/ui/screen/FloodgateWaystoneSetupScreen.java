package com.kalimero2.team.waystones.paper.ui.screen;

import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.TextUtil;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Arrays;
import java.util.List;

import static com.kalimero2.team.waystones.paper.PaperWayStones.manager;

public class FloodgateWaystoneSetupScreen implements GenericScreen {

    private final WaystoneSetupScreen setupScreen;

    protected FloodgateWaystoneSetupScreen(WaystoneSetupScreen setupScreen) {
        this.setupScreen = setupScreen;
    }


    @Override
    public void open(Player player) {
        open(player, null, "", 0, 0);
    }

    private void open(Player player, InputScreen.InputValidation lastValidation, String defaultName, int defaultVisibility, int defaultCategory) {
        CustomForm.Builder builder = CustomForm.builder().title(TextUtil.componentToString(setupScreen.getTitle()));

        Category[] categories = manager.getCategories().toArray(new Category[0]);

        if (setupScreen.getWaystone() != null && defaultName.isBlank()) {
            defaultName = setupScreen.getWaystone().name();
            defaultVisibility = setupScreen.getWaystone().visibility().id();
            Category waystoneCategory = setupScreen.getWaystone().category();
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(waystoneCategory)) {
                    defaultCategory = i;
                    break;
                }
            }
        }

        builder.input(setupScreen.getLabel(), setupScreen.getInput().placeholder(), defaultName);
        if (lastValidation != null) {
            builder.label(lastValidation.message());
        }

        builder.stepSlider("Visibility", List.of("Public", "Unlisted", "Private"), defaultVisibility);

        builder.dropdown("Category", Arrays.stream(categories).map(Category::name).toList(), defaultCategory);

        builder.validResultHandler(customFormResponse -> {
            String name = customFormResponse.asInput(0);
            Visibility visibility = Visibility.valueByNumber(customFormResponse.asStepSlider(1));
            Category category = categories[customFormResponse.asDropdown(2)];
            InputScreen.InputValidation inputValidation = setupScreen.getInput().onSubmitted().apply(player, new WaystoneSetupScreen.WaystoneSetupData(name, visibility, category));

            if (inputValidation.valid()) {
                return;
            }
            open(player, inputValidation, name, visibility.id(), category.id());
        });

        FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
        floodgatePlayer.sendForm(builder.build());
    }
}
