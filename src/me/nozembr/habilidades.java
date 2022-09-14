package me.nozembr;

import me.nozembr.events.Gladiator;
import me.wazup.kitbattle.Kit;
import me.wazup.kitbattle.Kitbattle;
import me.wazup.kitbattle.abilities.Ability;
import me.wazup.kitbattle.abilities.AbilityListener;
import me.wazup.kitbattle.abilities.AbilityManager;
import me.wazup.kitbattle.managers.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Iterator;

public class habilidades {

    KipvpAbilities plugin;
    public HashMap<String, Ability> abilities;

    public habilidades() {

        AbilityManager.getInstance().registerAbility(new Gladiator(null));
        AbilityManager.getInstance().updateKitAbilities();
        AbilityManager.getInstance().loadAbilitiesConfig();
    }
}
