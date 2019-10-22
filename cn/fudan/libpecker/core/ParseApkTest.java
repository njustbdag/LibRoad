package cn.fudan.libpecker.core;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
//import org.junit.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

public class ParseApkTest {
	public static void main(String arge[]) throws IOException, XmlPullParserException{
		 System.out.println(test("G:\\libdetectiongroundtruth\\APKset\\bacth2\\104_Job_Search_v1.11.0_apkpure.com.apk"));
	}
    //@Test
    public static String test(String apkPath) throws IOException, XmlPullParserException {
        String androidManifestPath = "E:\\AndroidManifest.xml";
        //String androidManifestPath = "G:\\libdetectiongroundtruth\\APKset\\apk2smali\\104_Job_Search_v1.11.0_apkpure.com.apk\\AndroidManifest.xml";
        String pkgname=null;
        if (extractAndroidManifest(apkPath, androidManifestPath)) {
        	//System.out.println(androidManifestPath);
        	pkgname=parseAndroidManifest(androidManifestPath);
        }
		return pkgname;
    }

    /**
     * 解压 Apk 文件，提取 AndroidManifest.xml
     */
    public static boolean extractAndroidManifest(String apkPath, String androidManifestPath) {
        ZipFile zipFile = null;
        InputStream inputStream = null;
        try {
            zipFile = new ZipFile(apkPath);
            ZipEntry entry = zipFile.getEntry("AndroidManifest.xml");
            inputStream = zipFile.getInputStream(entry);
            FileUtils.copyInputStreamToFile(inputStream, new File(androidManifestPath));
            System.out.println(androidManifestPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解析 AndroidManifest.xml
     */
    public static String parseAndroidManifest(String androidManifestPath) throws XmlPullParserException, IOException {
        AXmlResourceParser parser = new AXmlResourceParser();
        parser.open(new FileInputStream(androidManifestPath));
        StringBuilder indent = new StringBuilder(10);
        String pkgname=null;
        while (true) {
            int type = parser.next();
            if (type == XmlPullParser.END_DOCUMENT) {
                break;
            }
            switch (type) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    int namespaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
                    int namespaceCount = parser.getNamespaceCount(parser.getDepth());
                    /**for (int i = namespaceCountBefore; i != namespaceCount; ++i) {
                        System.out.printf("%sxmlns:%s=\"%s\"",
                            indent,
                            parser.getNamespacePrefix(i),
                            parser.getNamespaceUri(i));
                        System.out.println();
                    }**/
                    for (int i = 0; i != parser.getAttributeCount(); ++i) {
                    	//System.out.println(getAttributeValue(parser, i));
                    	if(parser.getAttributeName(i).equals("package")){
                            /**System.out.printf("%s%s%s=\"%s\"", 
                                    indent,
                                    getNamespacePrefix(parser.getAttributePrefix(i)),	
                                    parser.getAttributeName(i),
                                    getAttributeValue(parser, i));**/
                                //System.out.println();
                               // System.out.println(getAttributeValue(parser, i));
                                pkgname=getAttributeValue(parser, i);
                    	}

                        //System.out.println(parser.getAttributeName(i));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                case XmlPullParser.TEXT:
                    System.out.printf("%s%s", 
                        indent,
                        parser.getText());
                    System.out.println();
                default:
                    break;
            }
        }
		return pkgname;
    }

    private static String getNamespacePrefix(String prefix) {
        if (prefix == null || prefix.length() == 0) {
            return "";
        }
        return prefix + ":";
    }

    private static String getAttributeValue(AXmlResourceParser parser, int index) {
        int type = parser.getAttributeValueType(index);
        int data = parser.getAttributeValueData(index);
        if (type == TypedValue.TYPE_ATTRIBUTE) {
            return String.format("?%s%08X", getPackage(data), data);
            
        } 
        if (type == TypedValue.TYPE_INT_BOOLEAN) {
            return data != 0 ? "true" : "false";
        } 
        if (type == TypedValue.TYPE_DIMENSION) {
            return Float.toString(complexToFloat(data)) + DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        } 
        if (type == TypedValue.TYPE_FLOAT) {
            return String.valueOf(Float.intBitsToFloat(data));
        } 
        if (type == TypedValue.TYPE_FRACTION) {
            return Float.toString(complexToFloat(data)) + FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
        } 
        if (type == TypedValue.TYPE_INT_HEX) {
            return String.format("0x%08X", data);
        } 
        if (type == TypedValue.TYPE_REFERENCE) {
            return String.format("@%s%08X", getPackage(data), data);
        } 
        if (type == TypedValue.TYPE_STRING) {
            return parser.getAttributeValue(index);
        } 
        if (type >= TypedValue.TYPE_FIRST_COLOR_INT && type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return String.format("#%08X", data);
        }
        if (type >= TypedValue.TYPE_FIRST_INT && type <= TypedValue.TYPE_LAST_INT) {
            return String.valueOf(data);
        }
        return String.format("<0x%X, type 0x%02X>", data, type);
    }

    private static String getPackage(int id) {
        if (id >>> 24 == 1) {
            return "android:";
        }
        return "";
    }

    public static float complexToFloat(int complex) {
        return (float) (complex & 0xFFFFFF00) * RADIX_MULTS[(complex >> 4) & 3];
    }

    private static final float RADIX_MULTS[] = {
        0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
    };

    private static final String DIMENSION_UNITS[] = {
        "px","dip","sp","pt","in","mm","",""
    };

    private static final String FRACTION_UNITS[] = {
        "%","%p","","","","","",""
    };

}
