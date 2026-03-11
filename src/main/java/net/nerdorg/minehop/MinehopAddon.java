package net.nerdorg.minehop;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.nerdorg.minehop.config.ConfigWrapper;
import net.nerdorg.minehop.modules.Bunnyhopping;
import org.slf4j.Logger;

public class MinehopAddon extends MeteorAddon {
    public static final String MOD_ID = "minehop-meteor";
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOG.info("Initializing Minehop Meteor Addon");
        ConfigWrapper.loadConfig();
        Modules.get().add(new Bunnyhopping());
    }

    @Override
    public String getPackage() {
        return "net.nerdorg.minehop";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("njlent", "minehop");
    }
}
