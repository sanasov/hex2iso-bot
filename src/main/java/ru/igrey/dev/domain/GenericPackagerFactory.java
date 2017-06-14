package ru.igrey.dev.domain;

import lombok.extern.slf4j.Slf4j;
import org.jpos.core.SimpleConfiguration;
import java.io.InputStream;

import java.util.Properties;

/**
 * Created by dkhopryachkov on 12.05.2017.
 */
@Slf4j
public class GenericPackagerFactory {
    private static GenericPackagerEx storedPackager;

    public static GenericPackagerEx packager() {
        try {
            if (storedPackager != null) {
                return storedPackager;
            }
            try (InputStream inputStream = GenericPackagerFactory.class.getClassLoader().getResourceAsStream("mir_message.xml")) {
                Properties props = new Properties();
                props.setProperty("packager_attr", "packager");
                props.setProperty("packager_tag", "isopackager");
                props.setProperty("field_packager_tag", "isofieldpackager");
                props.setProperty("field_tag", "isofield");
                GenericPackagerEx packager = new GenericPackagerEx();
                packager.setConfiguration(new SimpleConfiguration(props));
                packager.readFile(inputStream);
                storedPackager = packager;
                return storedPackager;
            }
        } catch (Exception e) {
            throw new RuntimeException("Packager factory exception! Details:", e);
        }
    }
}