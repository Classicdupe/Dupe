package xyz.prorickey.classicdupe.customitems;

import org.bukkit.NamespacedKey;
import xyz.prorickey.classicdupe.ClassicDupe;

public class CIKeys {
    public static final NamespacedKey FBWAND;
    public static final NamespacedKey BURSTBOW;
    public static final NamespacedKey PVPPOT;
    public static final NamespacedKey PVPPOT2;

    static {
        FBWAND = new NamespacedKey(ClassicDupe.getPlugin(), "FBWAND");
        BURSTBOW = new NamespacedKey(ClassicDupe.getPlugin(), "BURSTBOW");
        PVPPOT = new NamespacedKey(ClassicDupe.getPlugin(), "PVPPot");
        PVPPOT2 = new NamespacedKey(ClassicDupe.getPlugin(), "PVPPot2");
    }
}
