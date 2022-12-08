package dev.JustRed23.uat.frontend;

import dev.JustRed23.abcm.parsing.IParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class InetAddressParser implements IParser<InetAddress> {

    public InetAddress parse(String value) {
        try {
            return InetAddress.getByName(value);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Class<?>> canParse() {
        return Collections.singletonList(InetAddress.class);
    }
}
