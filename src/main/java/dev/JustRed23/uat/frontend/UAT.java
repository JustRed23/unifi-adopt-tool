package dev.JustRed23.uat.frontend;

import dev.JustRed23.abcm.ConfigField;
import dev.JustRed23.abcm.Configurable;

import java.net.InetAddress;

@Configurable
public class UAT {

    @ConfigField(defaultValue = "true")
    public static boolean detectIP;

    @ConfigField(defaultValue = "192.168.0.1")
    public static InetAddress beginAddress;

    @ConfigField(defaultValue = "192.168.0.254")
    public static InetAddress endAddress;

    @ConfigField(defaultValue = "255.255.255.0")
    public static InetAddress subnetMask;

    //Credentials for the controller
    @ConfigField(defaultValue = "")
    public static String controllerAddress;

    @ConfigField(defaultValue = "8443")
    public static int controllerPort;

    @ConfigField(defaultValue = "admin")
    public static String controllerUsername;

    //Unifi default credentials
    @ConfigField(defaultValue = "ubnt")
    public static String defaultUsername;

    @ConfigField(defaultValue = "ubnt")
    public static String defaultPassword;
}
