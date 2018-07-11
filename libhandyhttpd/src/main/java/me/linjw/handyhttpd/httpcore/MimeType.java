package me.linjw.handyhttpd.httpcore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linjw on 18-7-11.
 */


public class MimeType {

    public static final MimeType APPLICATION_X_SHAR = new MimeType("application/x-shar", "shar");
    public static final MimeType AUDIO_X_MPEGURL = new MimeType("audio/x-mpegurl", "m3u");
    public static final MimeType APPLICATION_WINHLP = new MimeType("application/winhlp", "hlp");
    public static final MimeType VIDEO_X_SGI_MOVIE = new MimeType("video/x-sgi-movie", "movie");
    public static final MimeType IMAGE_X_ICON = new MimeType("image/x-icon", "ico");
    public static final MimeType APPLICATION_X_PKCS7_CERTIFICATES = new MimeType("application/x-pkcs7-certificates", "p7b", "spc");
    public static final MimeType TEXT_WEBVIEWHTML = new MimeType("text/webviewhtml", "htt");
    public static final MimeType APPLICATION_OCTET_STREAM = new MimeType("application/octet-stream", "*", "bin", "class", "dms", "exe", "lha", "lzh");
    public static final MimeType APPLICATION_X_MSCLIP = new MimeType("application/x-msclip", "clp");
    public static final MimeType TEXT_TAB_SEPARATED_VALUES = new MimeType("text/tab-separated-values", "tsv");
    public static final MimeType APPLICATION_X_INTERNET_SIGNUP = new MimeType("application/x-internet-signup", "ins", "isp");
    public static final MimeType APPLICATION_X_TAR = new MimeType("application/x-tar", "tar");
    public static final MimeType APPLICATION_X_PKCS7_MIME = new MimeType("application/x-pkcs7-mime", "p7c", "p7m");
    public static final MimeType APPLICATION_X_X509_CA_CERT = new MimeType("application/x-x509-ca-cert", "cer", "crt", "der");
    public static final MimeType APPLICATION_X_SV4CRC = new MimeType("application/x-sv4crc", "sv4crc");
    public static final MimeType IMAGE_X_XBITMAP = new MimeType("image/x-xbitmap", "xbm");
    public static final MimeType APPLICATION_ZIP = new MimeType("application/zip", "zip");
    public static final MimeType APPLICATION_X_GZIP = new MimeType("application/x-gzip", "gz");
    public static final MimeType APPLICATION_X_TCL = new MimeType("application/x-tcl", "tcl");
    public static final MimeType VIDEO_X_MSVIDEO = new MimeType("video/x-msvideo", "avi");
    public static final MimeType APPLICATION_X_TEXINFO = new MimeType("application/x-texinfo", "texi", "texinfo");
    public static final MimeType APPLICATION_OLESCRIPT = new MimeType("application/olescript", "axs");
    public static final MimeType APPLICATION_INTERNET_PROPERTY_STREAM = new MimeType("application/internet-property-stream", "acx");
    public static final MimeType VIDEO_X_MS_ASF = new MimeType("video/x-ms-asf", "asf", "asr", "asx");
    public static final MimeType APPLICATION_X_IPHONE = new MimeType("application/x-iphone", "iii");
    public static final MimeType APPLICATION_X_PERFMON = new MimeType("application/x-perfmon", "pma", "pmc", "pml", "pmr", "pmw");
    public static final MimeType APPLICATION_X_GTAR = new MimeType("application/x-gtar", "gtar");
    public static final MimeType APPLICATION_X_MSPUBLISHER = new MimeType("application/x-mspublisher", "pub");
    public static final MimeType APPLICATION_VNDMS_WORKS = new MimeType("application/vnd.ms-works", "wcm", "wdb", "wks", "wps");
    public static final MimeType APPLICATION_SET_REGISTRATION_INITIATION = new MimeType("application/set-registration-initiation", "setreg");
    public static final MimeType IMAGE_X_PORTABLE_ANYMAP = new MimeType("image/x-portable-anymap", "pnm");
    public static final MimeType APPLICATION_X_BCPIO = new MimeType("application/x-bcpio", "bcpio");
    public static final MimeType APPLICATION_VNDMS_PKISECCAT = new MimeType("application/vnd.ms-pkiseccat", "cat");
    public static final MimeType APPLICATION_X_CSH = new MimeType("application/x-csh", "csh");
    public static final MimeType APPLICATION_X_MSWRITE = new MimeType("application/x-mswrite", "wri");
    public static final MimeType MESSAGE_RFC822 = new MimeType("message/rfc822", "mht", "mhtml", "nws");
    public static final MimeType APPLICATION_X_SV4CPIO = new MimeType("application/x-sv4cpio", "sv4cpio");
    public static final MimeType APPLICATION_X_TEX = new MimeType("application/x-tex", "tex");
    public static final MimeType VIDEO_MPEG = new MimeType("video/mpeg", "mp2", "mpa", "mpe", "mpeg", "mpg", "mpv2");
    public static final MimeType APPLICATION_VNDMS_PKICERTSTORE = new MimeType("application/vnd.ms-pkicertstore", "sst");
    public static final MimeType APPLICATION_X_SH = new MimeType("application/x-sh", "sh");
    public static final MimeType APPLICATION_X_CPIO = new MimeType("application/x-cpio", "cpio");
    public static final MimeType IMAGE_X_RGB = new MimeType("image/x-rgb", "rgb");
    public static final MimeType TEXT_X_SETEXT = new MimeType("text/x-setext", "etx");
    public static final MimeType IMAGE_PIPEG = new MimeType("image/pipeg", "jfif");
    public static final MimeType APPLICATION_X_TROFF_MS = new MimeType("application/x-troff-ms", "ms");
    public static final MimeType X_WORLD_X_VRML = new MimeType("x-world/x-vrml", "flr", "vrml", "wrl", "wrz", "xaf", "xof");
    public static final MimeType APPLICATION_X_COMPRESS = new MimeType("application/x-compress", "z");
    public static final MimeType TEXT_SCRIPTLET = new MimeType("text/scriptlet", "sct");
    public static final MimeType APPLICATION_VNDMS_PROJECT = new MimeType("application/vnd.ms-project", "mpp");
    public static final MimeType APPLICATION_X_NETCDF = new MimeType("application/x-netcdf", "cdf", "nc");
    public static final MimeType APPLICATION_X_CDF = new MimeType("application/x-cdf", "cdf");
    public static final MimeType APPLICATION_X_MSMETAFILE = new MimeType("application/x-msmetafile", "wmf");
    public static final MimeType APPLICATION_X_MSTERMINAL = new MimeType("application/x-msterminal", "trm");
    public static final MimeType APPLICATION_SET_PAYMENT_INITIATION = new MimeType("application/set-payment-initiation", "setpay");
    public static final MimeType APPLICATION_X_MSACCESS = new MimeType("application/x-msaccess", "mdb");
    public static final MimeType IMAGE_X_CMX = new MimeType("image/x-cmx", "cmx");
    public static final MimeType IMAGE_X_CMU_RASTER = new MimeType("image/x-cmu-raster", "ras");
    public static final MimeType APPLICATION_X_MSCARDFILE = new MimeType("application/x-mscardfile", "crd");
    public static final MimeType APPLICATION_HTA = new MimeType("application/hta", "hta");
    public static final MimeType APPLICATION_X_MSSCHEDULE = new MimeType("application/x-msschedule", "scd");
    public static final MimeType APPLICATION_PICS_RULES = new MimeType("application/pics-rules", "prf");
    public static final MimeType TEXT_X_VCARD = new MimeType("text/x-vcard", "vcf");
    public static final MimeType APPLICATION_VNDMS_EXCEL = new MimeType("application/vnd.ms-excel", "xla", "xlc", "xlm", "xls", "xlt", "xlw");
    public static final MimeType APPLICATION_VNDMS_OUTLOOK = new MimeType("application/vnd.ms-outlook", "msg");
    public static final MimeType APPLICATION_X_JAVASCRIPT = new MimeType("application/x-javascript", "js");
    public static final MimeType APPLICATION_X_COMPRESSED = new MimeType("application/x-compressed", "tgz");
    public static final MimeType APPLICATION_X_SHOCKWAVE_FLASH = new MimeType("application/x-shockwave-flash", "swf");
    public static final MimeType APPLICATION_X_DVI = new MimeType("application/x-dvi", "dvi");
    public static final MimeType TEXT_HTML = new MimeType("text/html", "htm", "html", "stm");
    public static final MimeType APPLICATION_X_MSMONEY = new MimeType("application/x-msmoney", "mny");
    public static final MimeType AUDIO_BASIC = new MimeType("audio/basic", "au", "snd");
    public static final MimeType APPLICATION_X_HDF = new MimeType("application/x-hdf", "hdf");
    public static final MimeType APPLICATION_ODA = new MimeType("application/oda", "oda");
    public static final MimeType APPLICATION_MSWORD = new MimeType("application/msword", "doc", "dot");
    public static final MimeType APPLICATION_X_PKCS7_CERTREQRESP = new MimeType("application/x-pkcs7-certreqresp", "p7r");
    public static final MimeType APPLICATION_X_MSMEDIAVIEW = new MimeType("application/x-msmediaview", "m13", "m14", "mvb");
    public static final MimeType APPLICATION_X_MSDOWNLOAD = new MimeType("application/x-msdownload", "dll");
    public static final MimeType APPLICATION_PDF = new MimeType("application/pdf", "pdf");
    public static final MimeType APPLICATION_FRACTALS = new MimeType("application/fractals", "fif");
    public static final MimeType IMAGE_TIFF = new MimeType("image/tiff", "tif", "tiff");
    public static final MimeType APPLICATION_FUTURESPLASH = new MimeType("application/futuresplash", "spl");
    public static final MimeType IMAGE_X_PORTABLE_PIXMAP = new MimeType("image/x-portable-pixmap", "ppm");
    public static final MimeType APPLICATION_ENVOY = new MimeType("application/envoy", "evy");
    public static final MimeType APPLICATION_MAC_BINHEX40 = new MimeType("application/mac-binhex40", "hqx");
    public static final MimeType TEXT_PLAIN = new MimeType("text/plain", "bas", "c", "h", "txt");
    public static final MimeType IMAGE_JPEG = new MimeType("image/jpeg", "jpe", "jpeg", "jpg");
    public static final MimeType IMAGE_PNG = new MimeType("image/png", "png");
    public static final MimeType APPLICATION_YNDMS_PKIPKO = new MimeType("application/ynd.ms-pkipko", "pko");
    public static final MimeType AUDIO_X_WAV = new MimeType("audio/x-wav", "wav");
    public static final MimeType APPLICATION_X_USTAR = new MimeType("application/x-ustar", "ustar");
    public static final MimeType APPLICATION_X_TROFF_MAN = new MimeType("application/x-troff-man", "man");
    public static final MimeType IMAGE_IEF = new MimeType("image/ief", "ief");
    public static final MimeType APPLICATION_X_PKCS12 = new MimeType("application/x-pkcs12", "p12", "pfx");
    public static final MimeType AUDIO_MID = new MimeType("audio/mid", "mid", "rmi");
    public static final MimeType IMAGE_GIF = new MimeType("image/gif", "gif");
    public static final MimeType APPLICATION_VNDMS_POWERPOINT = new MimeType("application/vnd.ms-powerpoint", "pot", "pps", "ppt");
    public static final MimeType IMAGE_X_XWINDOWDUMP = new MimeType("image/x-xwindowdump", "xwd");
    public static final MimeType IMAGE_CIS_COD = new MimeType("image/cis-cod", "cod");
    public static final MimeType APPLICATION_RTF = new MimeType("application/rtf", "rtf");
    public static final MimeType AUDIO_X_PN_REALAUDIO = new MimeType("audio/x-pn-realaudio", "ra", "ram");
    public static final MimeType APPLICATION_POSTSCRIPT = new MimeType("application/postscript", "ai", "eps", "ps");
    public static final MimeType TEXT_IULS = new MimeType("text/iuls", "uls");
    public static final MimeType TEXT_RICHTEXT = new MimeType("text/richtext", "rtx");
    public static final MimeType VIDEO_QUICKTIME = new MimeType("video/quicktime", "mov", "qt");
    public static final MimeType APPLICATION_PKIX_CRL = new MimeType("application/pkix-crl", "crl");
    public static final MimeType APPLICATION_X_STUFFIT = new MimeType("application/x-stuffit", "sit");
    public static final MimeType APPLICATION_X_TROFF_ME = new MimeType("application/x-troff-me", "me");
    public static final MimeType APPLICATION_X_DIRECTOR = new MimeType("application/x-director", "dcr", "dir", "dxr");
    public static final MimeType IMAGE_X_PORTABLE_GRAYMAP = new MimeType("image/x-portable-graymap", "pgm");
    public static final MimeType IMAGE_X_PORTABLE_BITMAP = new MimeType("image/x-portable-bitmap", "pbm");
    public static final MimeType IMAGE_SVGXML = new MimeType("image/svg+xml", "svg");
    public static final MimeType APPLICATION_X_LATEX = new MimeType("application/x-latex", "latex");
    public static final MimeType TEXT_CSS = new MimeType("text/css", "css");
    public static final MimeType APPLICATION_VNDMS_PKISTL = new MimeType("application/vnd.ms-pkistl", "stl");
    public static final MimeType TEXT_H323 = new MimeType("text/h323", "323");
    public static final MimeType AUDIO_X_AIFF = new MimeType("audio/x-aiff", "aif", "aifc", "aiff");
    public static final MimeType TEXT_X_COMPONENT = new MimeType("text/x-component", "htc");
    public static final MimeType APPLICATION_X_TROFF = new MimeType("application/x-troff", "roff", "t", "tr");
    public static final MimeType VIDEO_X_LA_ASF = new MimeType("video/x-la-asf", "lsf", "lsx");
    public static final MimeType APPLICATION_X_WAIS_SOURCE = new MimeType("application/x-wais-source", "src");
    public static final MimeType IMAGE_X_XPIXMAP = new MimeType("image/x-xpixmap", "xpm");
    public static final MimeType AUDIO_MPEG = new MimeType("audio/mpeg", "mp3");
    public static final MimeType IMAGE_BMP = new MimeType("image/bmp", "bmp");
    public static final MimeType APPLICATION_X_PKCS7_SIGNATURE = new MimeType("application/x-pkcs7-signature", "p7s");
    public static final MimeType APPLICATION_PKCS10 = new MimeType("application/pkcs10", "p10");


    private static Map<String, MimeType> sMap;

    private String mType;
    private String[] mFileExts;

    public MimeType(String type, String... fileExts) {
        mType = type;
        mFileExts = fileExts;

        addToMap(this);
    }

    private static void addToMap(MimeType mimeType) {
        if (sMap == null) {
            sMap = new HashMap<>();
        }

        if (mimeType == null || mimeType.getFileExts() == null) {
            return;
        }

        for (String ext : mimeType.getFileExts()) {
            sMap.put(ext, mimeType);
        }
    }

    public static MimeType getMimeTypeByExt(String ext) {
        if (sMap == null || ext == null) {
            return APPLICATION_OCTET_STREAM;
        }
        MimeType mimeType = sMap.get(ext);
        return mimeType != null ? mimeType : APPLICATION_OCTET_STREAM;
    }


    public String getType() {
        return mType;
    }

    public String[] getFileExts() {
        return mFileExts;
    }
}
