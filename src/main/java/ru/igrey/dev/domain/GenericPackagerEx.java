/*
 * ++ _NSPK_ Front Office system. ++
 */

package ru.igrey.dev.domain;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.ReConfigurable;
import org.jpos.iso.*;
import org.jpos.util.Logger;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 * <p>The GenericPackagerEx class uses an XML config file to describe the layout of an ISOMessage.</p>
 * <p>This is a modified GenericPackager class from the jPos library. The most significant modifications
 * pertain to the ability to reconfigure XML tags used for XML documents (setConfiguration method).</p>
 */
public class GenericPackagerEx
        extends ISOBasePackager implements ReConfigurable {
    /* Values copied from ISOBasePackager
These can be changes using attributes on the isopackager node */
    private int maxValidField = 128;
    private boolean emitBitmap = true;
    private int bitmapField = 1;

    private String ID_ATTR = xmlConstants.ID_ATTR;
    private String CLASS_ATTR = xmlConstants.CLASS_ATTR;
    private String NAME_ATTR = xmlConstants.NAME_ATTR;
    private String LENGTH_ATTR = xmlConstants.LENGTH_ATTR;
    private String PAD_ATTR = xmlConstants.PAD_ATTR;
    private String TOKEN_ATTR = xmlConstants.TOKEN_ATTR;
    private String PACKAGER_TAG = xmlConstants.PACKAGER_TAG;
    private String FIELD_PACKAGER_TAG = xmlConstants.FIELD_PACKAGER_TAG;
    private String PACKAGER_ATTR = xmlConstants.PACKAGER_ATTR;
    private String FIELD_TAG = xmlConstants.FIELD_TAG;

    /**
     * Default constructor.
     */
    public GenericPackagerEx() {
        super();
    }

    /**
     * Create a GenericPackagerEx with the field descriptions
     * from an XML File
     *
     * @param filename The XML field description file
     */
    public GenericPackagerEx(String filename) throws ISOException {
        this();
        readFile(filename);
    }

    /**
     * Create a GenericPackagerEx with the field descriptions
     * from an XML InputStream
     *
     * @param input The XML field description InputStream
     */
    public GenericPackagerEx(InputStream input) throws ISOException {
        this();
        readFile(input);
    }

    public void setXmlConstants(xmlConstants constants) {
        ID_ATTR = xmlConstants.ID_ATTR;
        CLASS_ATTR = xmlConstants.CLASS_ATTR;
        NAME_ATTR = xmlConstants.NAME_ATTR;
        LENGTH_ATTR = xmlConstants.LENGTH_ATTR;
        PAD_ATTR = xmlConstants.PAD_ATTR;
        TOKEN_ATTR = xmlConstants.TOKEN_ATTR;
        PACKAGER_TAG = xmlConstants.PACKAGER_TAG;
        FIELD_PACKAGER_TAG = xmlConstants.FIELD_PACKAGER_TAG;
        PACKAGER_ATTR = xmlConstants.PACKAGER_ATTR;
        FIELD_TAG = xmlConstants.FIELD_TAG;
    }

    /**
     * Packager ModuleConfiguration.
     * <p/>
     * <ul>
     * <li> ID_ATTR            = "id" by default
     * <li> CLASS_ATTR         = "class" by default
     * <li> NAME_ATTR          = "name" by default
     * <li> LENGTH_ATTR        = "length" by default
     * <li> PAD_ATTR           = "pad" by default
     * <li> TOKEN_ATTR         = "token" by default
     * <li> PACKAGER_TAG       = "ipmpackager" by default
     * <li> FIELD_PACKAGER_TAG = "ipmfieldpackager" by default
     * <li> PACKAGER_ATTR      = "outPackager" by default
     * <li> FIELD_TAG          = "ipmfield" by default
     * </ul>
     *
     * @param cfg ModuleConfiguration
     */
    public void setConfiguration(Configuration cfg)
            throws ConfigurationException {
        try {
            String loggerName = cfg.get(xmlConstants.PKG_LOGGER);
            if (loggerName != null)//NOPMD
                setLogger(Logger.getLogger(loggerName),
                        cfg.get(xmlConstants.PKG_REALM));

            String str;

            str = cfg.get("ID_ATTR");
            if (str != null && str.trim().length() != 0) ID_ATTR = str;//NOPMD

            str = cfg.get("CLASS_ATTR");
            if (str != null && str.trim().length() != 0) CLASS_ATTR = str;//NOPMD

            str = cfg.get("NAME_ATTR");
            if (str != null && str.trim().length() != 0) NAME_ATTR = str;//NOPMD

            str = cfg.get("LENGTH_ATTR");
            if (str != null && str.trim().length() != 0) LENGTH_ATTR = str;//NOPMD

            str = cfg.get("PAD_ATTR");
            if (str != null && str.trim().length() != 0) PAD_ATTR = str;//NOPMD

            str = cfg.get("TOKEN_ATTR");
            if (str != null && str.trim().length() != 0) TOKEN_ATTR = str;//NOPMD

            str = cfg.get("PACKAGER_TAG");
            if (str != null && str.trim().length() != 0) PACKAGER_TAG = str;//NOPMD

            str = cfg.get("FIELD_PACKAGER_TAG");
            if (str != null && str.trim().length() != 0) FIELD_PACKAGER_TAG = str;//NOPMD

            str = cfg.get("PACKAGER_ATTR");
            if (str != null && str.trim().length() != 0) PACKAGER_ATTR = str;//NOPMD

            str = cfg.get("FIELD_TAG");
            if (str != null && str.trim().length() != 0) FIELD_TAG = str;//NOPMD

            String pkgConfig = cfg.get(xmlConstants.PKG_CONFIG);
            if ( pkgConfig != null && pkgConfig.length() > 0 )//NOPMD
                readFile( pkgConfig );
        }
        catch (ISOException e) {
            throw new ConfigurationException(e);
        }
    }

    protected int getMaxValidField() {
        return maxValidField;
    }

    protected boolean emitBitMap() {
        return emitBitmap;
    }

    protected ISOFieldPackager getBitMapfieldPackager() {
        return fld[bitmapField];
    }

    /**
     * Parse the field descriptions from an XML file.
     * <p/>
     * <pre>
     * Uses the sax parser specified by the system property 'sax.parser'
     * The default parser is org.apache.xerces.parsers.SAXParser
     * </pre>
     *
     * @param filename The XML field description file name
     */
    public void readFile(String filename) throws ISOException {
        try {
            createXMLReader().parse(filename);
        }
        catch (Exception e) {
            throw new ISOException(e);
        }
    }

    /**
     * Parse the field descriptions from an XML InputStream.
     * <p/>
     * <pre>
     * Uses the sax parser specified by the system property 'sax.parser'
     * The default parser is org.apache.xerces.parsers.SAXParser
     * </pre>
     *
     * @param input The XML field description InputStream
     */
    public void readFile(InputStream input) throws ISOException {
        try {
            createXMLReader().parse(new InputSource(input));
        }
        catch (Exception e) {
            throw new ISOException(e);
        }
    }

    private XMLReader createXMLReader() throws SAXException {
        XMLReader reader = XMLReaderFactory.createXMLReader(
                System.getProperty("sax.parser",
                        "org.apache.xerces.parsers.SAXParser"));

        reader.setFeature("http://xml.org/sax/features/validation", true);
        GenericContentHandler handler = new GenericContentHandler();
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        return reader;
    }


    private void setGenericPackagerParams(Attributes atts) {
        String maxField = atts.getValue("MAX_VALID_FIELD_ATTR");
        String emitBmap = atts.getValue("EMIT_BITMAP_ATTR");
        String bmapfield = atts.getValue("BITMAP_FIELD_ATTR");

        if (maxField != null)                                                                           //NOPMD
            maxValidField = Integer.parseInt(maxField);

        if (emitBmap != null)//NOPMD
            emitBitmap = Boolean.valueOf(emitBmap).booleanValue();

        if (bmapfield != null)//NOPMD
            bitmapField = Integer.parseInt(bmapfield);
    }

    @SuppressWarnings("unchecked")
    public class GenericContentHandler extends DefaultHandler {
        private Stack fieldStack;

        public void startDocument() {
            fieldStack = new Stack();
        }

        public void endDocument() throws SAXException {
            if (!fieldStack.isEmpty()) {
                throw new SAXException("Format error in XML Field Description File");
            }
        }

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
                throws SAXException {
            try {
                String id = atts.getValue(ID_ATTR);
                String type = atts.getValue(CLASS_ATTR);
                String name = atts.getValue(NAME_ATTR);
                String size = atts.getValue(LENGTH_ATTR);
                String pad = atts.getValue(PAD_ATTR);
                String token = atts.getValue(TOKEN_ATTR);

                if (localName.equals(PACKAGER_TAG)) {
                    // Stick a new Hashtable on stack to collect the fields
                    fieldStack.push(new Hashtable());

                    setGenericPackagerParams(atts);
                }

                if (localName.equals(FIELD_PACKAGER_TAG)) {
                    /*
                    For a isofield outPackager node push the following fields
                    onto the stack.
                    1) an Integer indicating the field ID
                    2) an instance of the specified ISOFieldPackager class
                    3) an instance of the specified ISOBasePackager (msgPackager) class
                    4) a Hashtable to collect the subfields
                    */
                    String packager = atts.getValue(PACKAGER_ATTR);
                    if (packager == null) {
                        throw new IllegalArgumentException("Packager attribute missing: " + PACKAGER_ATTR);
                    }

                    fieldStack.push(new Integer(id));

                    ISOFieldPackager f;
                    f = (ISOFieldPackager) Class.forName(type).newInstance();
                    f.setDescription(name);
                    f.setLength(Integer.parseInt(size));
                    f.setPad(new Boolean(pad).booleanValue());//NOPMD
                    if (f instanceof ISOStringFieldPackager) {//NOPMD
                        
                    }
                    // Modified for using IF_TBASE
                    if (f instanceof IF_TBASE) {
                        ((IF_TBASE) f).setToken(token);
                    }
                    fieldStack.push(f);

                    ISOBasePackager p;
                    p = (ISOBasePackager) Class.forName(packager).newInstance();
                    if (p instanceof GenericPackagerEx) {
                        GenericPackagerEx gp = (GenericPackagerEx) p;
                        gp.setGenericPackagerParams(atts);
                    }
                    fieldStack.push(p);

                    fieldStack.push(new Hashtable());
                }

                if (localName.equals(FIELD_TAG)) {
                    Class c = Class.forName(type);
                    ISOFieldPackager f;
                    f = (ISOFieldPackager) c.newInstance();
                    f.setDescription(name);
                    f.setLength(Integer.parseInt(size));
                    f.setPad(new Boolean(pad).booleanValue());//NOPMD
                    // Modified for using IF_TBASE
                    if (f instanceof IF_TBASE) {
                        ((IF_TBASE) f).setToken(token);
                    }
                    // Insert this new isofield into the Hashtable
                    // on the top of the stack using the fieldID as the key
                    Hashtable ht = (Hashtable) fieldStack.peek();
                    ht.put(new Integer(id), f);
                }
            }
            catch (Exception ex) {
                throw new SAXException(ex);
            }
        }

        /**
         * Convert the ISOFieldPackagers in the Hashtable
         * to an array of ISOFieldPackagers
         */
        private ISOFieldPackager[] makeFieldArray(Hashtable tab) {
            int maxField = 0;

            // First find the largest field number in the Hashtable
            for (Enumeration e = tab.keys(); e.hasMoreElements();) {
                int n = ((Integer) e.nextElement()).intValue();
                if (n > maxField) maxField = n;//NOPMD
            }

            // Create the array
            ISOFieldPackager fld[] = new ISOFieldPackager[maxField + 1];

            // Populate it
            for (Enumeration e = tab.keys(); e.hasMoreElements();) {
                Integer key = (Integer) e.nextElement();
                fld[key.intValue()] = (ISOFieldPackager) tab.get(key);
            }
            return fld;
        }

        public void endElement(String namespaceURI, String localName, String qName) {
            if (localName.equals(PACKAGER_TAG)) {
                Hashtable tab = (Hashtable) fieldStack.pop();

                setFieldPackager(makeFieldArray(tab));
            }

            if (localName.equals(FIELD_PACKAGER_TAG)) {
                // Pop the 4 entries off the stack in the correct order
                Hashtable tab = (Hashtable) fieldStack.pop();

                ISOBasePackager msgPackager = (ISOBasePackager) fieldStack.pop();
                msgPackager.setFieldPackager(makeFieldArray(tab));
                msgPackager.setLogger(getLogger(), "Generic Packager");

                ISOFieldPackager fieldPackager = (ISOFieldPackager) fieldStack.pop();

                Integer fno = (Integer) fieldStack.pop();

                // Create the ISOMsgField outPackager with the retrieved msg and field Packagers
                ISOMsgFieldPackager mfp =
                        new ISOMsgFieldPackager(fieldPackager, msgPackager);

                // Add the newly created ISOMsgField outPackager to the
                // lower level field stack

                tab = (Hashtable) fieldStack.peek();
                tab.put(fno, mfp);
            }
        }

        // ErrorHandler Methods
        public void error(SAXParseException ex) throws SAXException {
            throw ex;
        }

        public void fatalError(SAXParseException ex) throws SAXException {
            throw ex;
        }
    }
}

