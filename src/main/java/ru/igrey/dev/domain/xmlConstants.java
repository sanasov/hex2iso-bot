/*
 * ++ _NSPK_ Front Office system. ++
 */

package ru.igrey.dev.domain;

/**
 * The xmlConstants class defines typical tag names used in the XML files
 */
public class xmlConstants {//NOPMD

    // Most common tags
    public static final String ISOMSG_TAG    = "ipmmsg";
    public static final String ISOFIELD_TAG  = "field";
    public static final String ID_ATTR       = "id";
    public static final String VALUE_ATTR    = "value";
    public static final String TYPE_ATTR     = "type";
    public static final String CLASS_ATTR    = "class";
    public static final String NAME_ATTR     = "name";
    public static final String LENGTH_ATTR   = "length";
    public static final String PAD_ATTR      = "pad";
    public static final String TOKEN_ATTR    = "token";
    public static final String TYPE_BINARY   = "binary";
    public static final String TYPE_BITMAP   = "bitmap";
    public static final String PACKAGER_ATTR        = "packager";
    // Generic Packager Params
    public static final String MAX_VALID_FIELD_ATTR = "maxValidField";
    public static final String EMIT_BITMAP_ATTR = "emitBitmap";
//    public static final String PACKAGER_ATTR        = "outPackager";
    public static final String BITMAP_FIELD_ATTR    = "bitmapField";
    public static final String PKG_LOGGER           = "outPackager-logger";
    public static final String PKG_REALM            = "outPackager-realm";
    public static final String PKG_CONFIG           = "outPackager-config";
    public static final String VALIDATION_ID_ATTR   = "validation-ID";
    // Generic Packager configuration file constants
    public static String PACKAGER_TAG = "ipmpackager";
    public static String FIELD_PACKAGER_TAG = "ipmfieldpackager";
    public static String FIELD_TAG = "ipmfield";
}
