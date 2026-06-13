package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class TradeListener implements Listener {

    private final PaperWayStones plugin;

    private final MerchantRecipe waystoneRecipe;
    private final MerchantRecipe portableWaystoneRecipe;

    private final static int MAX_TRADES_WAYSTONE = 1;
    private final static int MAX_TRADES_PORTABLE_WAYSTONE = 1;
    private final static float CHANCE_ADDITIONAL_WAYSTONE_TRADE = 0;
    private final static float CHANCE_ADDITIONAL_PORTABLE_WAYSTONE_TRADE = 0;
    private final static float CHANCE_REPLACE_TRADES = 0.2f;
    private final static boolean EXPERIENCE_REWARD = false;

    public TradeListener(PaperWayStones plugin){
        this.plugin = plugin;

        waystoneRecipe = new MerchantRecipe(plugin.getStatic(), 0, MAX_TRADES_WAYSTONE, EXPERIENCE_REWARD);
        waystoneRecipe.addIngredient(new ItemStack(Material.NETHERITE_INGOT, 2));
        waystoneRecipe.addIngredient(new ItemStack(Material.ENDER_EYE, 1));

        portableWaystoneRecipe = new MerchantRecipe(plugin.getPortable(), 0, MAX_TRADES_PORTABLE_WAYSTONE, EXPERIENCE_REWARD);
        portableWaystoneRecipe.addIngredient(plugin.getStatic());
        portableWaystoneRecipe.addIngredient(new ItemStack(Material.NETHER_STAR, 1));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() != EntityType.WANDERING_TRADER) return;
        if (!(event.getEntity() instanceof WanderingTrader)) return;

        WanderingTrader trader = (WanderingTrader) event.getEntity();
        List<MerchantRecipe> trades = new ArrayList<>();

        if (Math.random() < CHANCE_REPLACE_TRADES) {
            trades.add(waystoneRecipe);
            trades.add(portableWaystoneRecipe);
            trader.setRecipes(trades);
            return;
        }

        if (Math.random() < CHANCE_ADDITIONAL_WAYSTONE_TRADE) {
            MerchantRecipe wayStone = new MerchantRecipe(plugin.getStatic(), 0, MAX_TRADES_WAYSTONE, EXPERIENCE_REWARD);
            trades.add(wayStone);
        }

        if (Math.random() < CHANCE_ADDITIONAL_PORTABLE_WAYSTONE_TRADE) {
            MerchantRecipe portableWayStone = new MerchantRecipe(plugin.getPortable(), 0, MAX_TRADES_PORTABLE_WAYSTONE, EXPERIENCE_REWARD);
            trades.add(portableWayStone);
        }

        trades.addAll(trader.getRecipes());
        trader.setRecipes(trades);
    }


}
