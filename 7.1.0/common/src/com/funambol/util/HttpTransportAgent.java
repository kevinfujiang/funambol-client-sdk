/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import com.funambol.util.Log;
import com.funambol.util.StreamReaderFactory;


//#ifdef isBlackberry 
//# import com.funambol.util.BlackberryHelper;
//#elif isBlackberry_plugin
//# import com.funambol.util.BlackberryHelper;
//#endif
/**
 *  Represents a HTTP client implementation
 **/
public final class HttpTransportAgent implements TransportAgent {

    // --------------------------------------------------------------- Constants
    private static final int NUM_RETRY = 3;
    private static final String PROP_MICROEDITION_CONFIGURATION =
            "microedition.configuration";
    private static final String PROP_CONTENT_LANGUAGE =
            "Content-Language";
    private static final String PROP_CONTENT_LENGTH =
            "Content-Length";
    private static final String PROP_UNCOMPR_LENGHT =
            "Uncompressed-Content-Length";
    private static final String PROP_CONTENT_TYPE =
            "Content-Type";
    private static final String PROP_MICROEDITION_LOCALE =
            "microedition.locale";
    private static final String PROP_MICROEDITION_PROFILES =
            "microedition.profiles";
    private static final String PROP_USER_AGENT =
            "User-Agent";
    // specific property to send device identity to the server
    private static final String PROP_DEVICE_AGENT =
            "Device-Agent";
    // Proprietary http header to avoid bugs of Nokia S60 3ed. FP1
    // It forces the server to set a 'Set-Cookie' header not empty
    private static final String PROP_FORCE_COOKIES =
            "x-funambol-force-cookies";
    private static final String PROP_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String PROP_CONTENT_ENCODING = "Content-Encoding";
    private static final String PROP_DATE = "Date";
    private static final String PROP_SIZE_THRESHOLD =
            "Size-Threshold";
    private static final String COMPRESSION_TYPE_GZIP = "gzip";
    private static final String COMPRESSION_TYPE_ZLIB = "deflate";
    private final String userAgent;
    private final String charset;
    private final int OPEN_CONNECTION = 0;
    private final int WRITE_REQUEST = 1;
    private final int READ_RESPONSE = 2;
    private final int RESPONSE_CORRECTLY_PROCESSED = 3;

    // ------------------------------------------------------------ Private Data
    private String requestURL;
    private String contentType;
    // Compression parameters
    private int sizeThreshold;
    private boolean enableCompression;
    private String responseDate;
    private boolean forceCookies;
    private int uncompressedLength;
    private int retryOnWrite;
    private int counter;
    private int status = OPEN_CONNECTION;
    private static HttpConnection c = null;
    private static InputStream is = null;
    private static OutputStream os = null;
    private long CONNECTION_SLEEP_TIME = 10000;

    // ------------------------------------------------------------ Constructors
    /**
     * Create a new HttpTransportAgent. 
     * @param requestURL is the url where to send the request; must not be null
     * @param compress if true the http compression is enabled, disabled 
     * otherwise
     * @param forceCookies
     */
    public HttpTransportAgent(String requestURL, boolean compress, 
                                                         boolean forceCookies) {
        this(requestURL, null, null, compress, forceCookies);
    }

    /**
     * Create a new HttpTransportAgent using the default charset.
     * @param requestURL is the url where to send the request; must not be null
     * @param userAgent the user agent parameter to be filled into the http 
     * headers
     * @param compress if true the http compression is enabled, disabled 
     * otherwise
     * @param forceCookies if true sets http headers to force the use of cookies 
     * instead of Url Rewriting to manage the current http session.
     */
    public HttpTransportAgent(String requestURL, final String userAgent,
            boolean compress, boolean forceCookies) {
        this(requestURL, userAgent, null, compress, forceCookies);
    }

    /**
     * Initialize a new HttpTransportAgent with a URL and a charset to use.
     *
     * @param requestURL must be non-null
     * @param userAgent a string to be used as userAgent.
     * @param charset a valid charset, the device charset is used by default.
     *
     */
    public HttpTransportAgent(String requestURL,
            final String userAgent,
                                final String charset, 
                                boolean compress, 
                                boolean forceCookies) {

        if (requestURL == null) {
            throw new NullPointerException(
                    "TransportAgent: request URL parameter is null");
        }

        //#ifdef isBlackberry
        //# if (retryOnWrite < BlackberryHelper.getConfigCount()) {
        //#    retryOnWrite = BlackberryHelper.getConfigCount();
        //# Log.debug("[httpTransportAgent] bb found, increasing connection retry to " +
        //#        retryOnWrite);
        //# }
        //#elif isBlackberry_plugin
        //# if (retryOnWrite < BlackberryHelper.getConfigCount()) {
        //#    retryOnWrite = BlackberryHelper.getConfigCount();
        //# Log.debug("[httpTransportAgent] bb found, increasing connection retry to " +
        //#        retryOnWrite);
        //# }
        //#endif
        retryOnWrite = NUM_RETRY;
        this.userAgent = userAgent;
        this.requestURL = requestURL;
        this.charset = charset;
        this.sizeThreshold = 0;
        this.enableCompression = compress;
        this.responseDate = null;
        this.forceCookies = forceCookies;
        Log.info("HttpTransportAgent - enableCompression: " + enableCompression);
        Log.info("HttpTransportAgent - forceCookies: " + forceCookies);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Send a message using the default (UTF-8) charset.
     * @param requestURL must be non-null
     * @return String formatted http response
     * @throws CodedException when the connection cannot be established with the 
     * server because the implementation tries to access a not existent url 
     * or when there are network coverage problems 
     */
    public String sendMessage(String request) throws CodedException {
        return sendMessage(request, this.charset);
    }

    /**
     * Send the http request specifying the required encoding charset for the 
     * http headers. and read the response
     * @param request the http request body
     * @param charset the charset to be included into the http headers
     * @return String formatted http response
     * @throws CodedException when the connection cannot be established with the 
     * server because the implementation tries to access a not existent url 
     * or when there are network coverage problems 
     */
    public String sendMessage(String request, String charset)
            throws CodedException {
        byte[] indata = null;
        byte[] outdata = null;

        if (charset != null) {
            try {
                indata = request.getBytes(charset);
            } catch (UnsupportedEncodingException uee) {
                Log.error("Charset " + charset + " not supported. Using default");
                charset = null;
                indata = request.getBytes();
            }
        } else {
            indata = request.getBytes();
        }

        request = null;
        outdata = sendMessage(indata);

        indata = null;

        if (outdata == null) {
            String msg = "Response data null";
            Log.error("[sendMessage] " + msg);
            throw new CodedException(CodedException.DATA_NULL, msg);
        } else {

            if (charset != null) {
                try {
                    return new String(outdata, charset);
                } catch (UnsupportedEncodingException uee) {
                    Log.error("Charset " + charset + " not supported. Using default");
                    charset = null;
                    return new String(outdata);
                }
            } else {
                return new String(outdata);
            }
        }
    }

    /**
     * Send the http request and read the response
     * @param request the http request body
     * @return byte[] raw http response
     * @throws CodedException when the connection cannot be established with the 
     * server because the implementation tries to access a not existent url 
     * or when there are network coverage problems 
     */
      public byte[] sendMessage(byte[] request) throws CodedException  {

        byte[] data = null;
        try {
            //Open up a HTTP connection and sends the request to the server 
            //writing on the output stream
            writeRequest(request);

            //Read the server response from the input stream returned by the 
            //server and set the related byte array 
            data = readResponse(data);

            //Exits the cicle only when the response is correctly read
            if (status == RESPONSE_CORRECTLY_PROCESSED) {
                return data;
            }

        } catch (ConnectionNotFoundException e) {
            //This exception is implementation dependent and it is thrown 
            //when the requested url string is incorrect. This can depend by
            //1) The requested URL doesn't exist
            //doesn't exist);
            //2) The required protocol (i.e. http://) is not correctly 
            //specified
            String msg = "HttpTransportAgent: can't open connection -->" + e.toString();
            Log.error(msg);
            throw new CodedException(CodedException.CONN_NOT_FOUND, msg);
        } catch (IllegalArgumentException e) {
            String msg = "HttpTransportAgent: invalid argument for connection --> " + e.toString();
            Log.error(msg);
            throw new CodedException(CodedException.ILLEGAL_ARGUMENT, msg);
        } finally {
            clear();
        }
        //Return the byte[] corresponding to the read response 
        return data;
    }
    
    /**
     * Enable the http "gzip" compression parameter usage
     * @param enable enables "gzip" http header parameter to be written if true
     */
    public void enableCompression(boolean enable) {
        this.enableCompression = enable;
    }

    /**
     * Set the http "Size-Threshold" header parameter
     * @param threshold is the "Size-Threshold" value to be added to http 
     * headers
     */
    public void setThreshold(int threshold) {
        this.sizeThreshold = threshold;
    }

    /**
     * Set the request's url
     * @param requestURL the request's destination url
     */
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    /**
     * Get the last response date
     * @return the last response date (String format), 
     * if available, null otherwise
     */
    public String getResponseDate() {
        return responseDate;
    }

    /**
     * Set the number of http writing attempts
     * @param retryOnWrite the number of attempts to write http requests
     */
    public void setRetryOnWrite(int retryOnWrite) {
        this.retryOnWrite=retryOnWrite;
    }

    //---------------------------------------------------------- Private methods
    private void openConnection(byte[] request, int i) throws IOException {
        status = OPEN_CONNECTION;
        //#ifdef low_mem
//#                 System.gc();        //XXX
//#                 try { Thread.sleep(1); } catch (InterruptedException ex) {
//#                     Log.error(this, "interruptedException in sendMessage");
//#                     ex.printStackTrace();
//#                 }
                //#endif


                String url = requestURL;



                //#ifdef isBlackberry
                //# if (BlackberryHelper.workingConfigHasBeenFound()) {
                //#     Log.debug("[HTTPTransportAgent] we have a working conifg!");
                //#     String options = BlackberryHelper.getOptions(i);
                //#     Log.debug("[sendMessage] Using BlackBerry Options: " + options);
                //#     url = requestURL + options;
                //# } else if (BlackberryHelper.isOkToUseConfig(i)) {
                //#      String options = BlackberryHelper.getOptions(i);
                //#      Log.debug("[sendMessage] Using BlackBerry Options: " + options);
                //#      url = requestURL + options;
                //# } else {
                //#     Log.debug("[HHTPTransportAgent] config " + i + " can not be used. should stop everything");
                //# //i = NUM_RETRY-1;
                //# throw new ConnectionDeniedException("config cannot be used!");
                //TODO: stop stuff here
                //# //break;
                //#     }
                //#elif isBlackberry_plugin
                //# if (BlackberryHelper.workingConfigHasBeenFound()) {
                //#     Log.debug("[HTTPTransportAgent] we have a working conifg!");
                //#     String options = BlackberryHelper.getOptions(i);
                //#     Log.debug("[sendMessage] Using BlackBerry Options: " + options);
                //#     url = requestURL + options;
                //# } else if (BlackberryHelper.isOkToUseConfig(i)) {
                //#      String options = BlackberryHelper.getOptions(i);
                //#      Log.debug("[sendMessage] Using BlackBerry Options: " + options);
                //#      url = requestURL + options;
                //# } else {
                //#     Log.debug("[HHTPTransportAgent] config " + i + " can not be used. should stop everything");
                //# //i = NUM_RETRY-1;
                //# throw new ConnectionDeniedException("config cannot be used!");
                //TODO: stop stuff here
                //# //break;
                //#     }
                //#endif
                Log.info("[sendMessage] url: [" + url + "]");
                /*
                 * Emulating only cookie support
                 
                        int jsIndex = url.indexOf("jsessionid");
                        if (jsIndex != -1) {
                            url = url.substring(0, jsIndex - 1);
                        }
                 */   
                
                c = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);


                //Request Configuration: Added ACCEPT ENCODING Parameter
                setConfig(c, request.length);
    }

    private void writeRequest(byte[] request) 
                throws IllegalArgumentException, ConnectionNotFoundException, 
                                                                 CodedException {
        for (int i=0; i<retryOnWrite; i++)  {
            try {
                openConnection(request, i);
                status = WRITE_REQUEST;
                //Write the message to send into the stream
                //forceBreakConnection();
                os = c.openOutputStream();
                //XXX if the mapping message is not sent due to network problem 
                //duplicated items arrive on the client
                os.write(request);
                Log.info("[sendMessage] message sent at attempt " + (i+1) + ", waiting for response.");
                break;
            } catch (IOException e) {
                clear();
                if (status == OPEN_CONNECTION) {
                    Log.error("[writeRequest.openConnection] Attempt n." + (i + 1) + " failed. Retrying...");
                    if (i == retryOnWrite - 1) {
                        //New Read Exception or New writeException
                        throw new CodedException(CodedException.CONN_NOT_FOUND,
                                            "Network problem: Cannot send the request to the server");
                    } else {
                        waitToConnect();
                    }
                } else if (status == WRITE_REQUEST) {
                    Log.error("[writeRequest] Attempt n." + (i + 1) + " failed. Retrying...");
                    if (i == retryOnWrite - 1) {
                        //New Read Exception or New writeException
                        throw new CodedException(CodedException.WRITE_SERVER_REQUEST_ERROR,
                                                 "Network problem: Cannot send the request to the server");
                    } else {
                        waitToConnect();
                    }
                }
            }
        }
    }

    private void waitToConnect() {
        try {
            long startTime = System.currentTimeMillis();
            Log.info("Connection timer started");
            Thread.sleep(CONNECTION_SLEEP_TIME);
            long now = System.currentTimeMillis();
            Log.info("Retrying after " +  (now - startTime) + " msec");
        } catch (InterruptedException ex) {
            Log.error("Connection timer failed");
            ex.printStackTrace();
        }
    }
    
    private byte[] readResponse(byte[] data) throws CodedException {
        status = READ_RESPONSE;

            try {
                is = c.openInputStream();
                //Insert here the broken connection test code
                Log.info("[sendMessage] message received");

                logHeaders(c);

                long len = c.getLength();
                Log.info("HttpResponse length: " + len);

                // Check http error
                int httpCode = c.getResponseCode();
                Log.info("[sendMessage] Http Code: " + httpCode);
                if (httpCode != c.HTTP_OK) {
                    //throw new CodedException(SyncException.SERVER_ERROR,
                    Log.error("Http error: code=[" + httpCode + "] msg=[" + c.getResponseMessage() + "]");
                        String msg = " Http error: code=[" + httpCode + "] msg=[" + c.getResponseMessage() + "]";
                        throw new CodedException(CodedException.CONN_NOT_FOUND, msg);
                } else {
                    responseDate = c.getHeaderField(PROP_DATE);
                    Log.info("[sendMessage] Date from server: " + responseDate);

                    contentType = c.getHeaderField(PROP_CONTENT_ENCODING);
                    Log.info("[sendMessage] Encoding Response Type from server: " + contentType);

                    uncompressedLength = c.getHeaderFieldInt(PROP_UNCOMPR_LENGHT, -1);
                    Log.info("[sendMessage] Uncompressed Content Lenght: " + uncompressedLength);

                    if ((len == -1) && (responseDate == null) && (contentType == null)) {
                        Log.error("Http error: httpCode=[" + httpCode + "] msg=[" + c.getResponseMessage() + "] len=[" + len + "] date=[" + responseDate + "] contentType=[" + contentType + "]");
                        Log.error("[sendMessage] Error in http response, not reading stream...");
                    } else {
                        data = StreamReaderFactory.getStreamReader(contentType).readStream(is, (int) len);
                        Log.debug("data.length: " + data.length);

                        if ((uncompressedLength != -1) && (data.length != uncompressedLength)) {
                            // try again but use uncompressed mode
                            //enableCompression = false;
                            //continue;

                            Log.error("[sendMessage] Error reading compressed response");
                            Log.error("[sendMessage] Trying with uncompressed.");
                            throw new CodedException(CodedException.ERR_READING_COMPRESSED_DATA,
                                                     "Error reading compressed response");

                        }

                        Log.info("[sendMessage] Stream correctly processed.");

                        //#ifdef isBlackberry
                        //# if  (BlackberryHelper.getSavedConfigID()<0) {
                        //#     BlackberryHelper.saveCurrentConfig();
                        //# }
                        //#elif isBlackberry_plugin
                        //# if  (BlackberryHelper.getSavedConfigID()<0) {
                        //#     BlackberryHelper.saveCurrentConfig();
                        //# }
                        //#endif
                        /*
                        if (UIController.WAP_SIMULATION) {
                        Log.debug("##### WAP SIMULATION with compression error#######");
                        UIController.WAP_SIMULATION = false;
                        throw new CompressedSyncException("Error reading compressed response");
                        }*/
                        status = RESPONSE_CORRECTLY_PROCESSED;
                    }
                }
            } catch (CodedException e) {
              
                String msg = "[HttpTransportAgent] user denied connection" + e.toString();
                Log.error(msg);
                throw new CodedException(CodedException.CONNECTION_BLOCKED_BY_USER, msg);
                
            } catch (IOException ioe) {
                String msg = "HttpTransportAgent: error reading server response --> " + ioe.toString();
                Log.error(msg);
                //New Read Exception or New writeException
                throw new CodedException(CodedException.READ_SERVER_RESPONSE_ERROR,
                                         "Network problem: Cannot read the server response");
            }
        return data;
    }
    
    /**
     * Simulate connections broken when the final message is sent in test mode
     */
    /*private void breakConnection() throws IOException {
        counter++;
        Log.info("Counter=" + counter);
        Log.info("Retries on read: " + retryOnWrite);
                        
        if (counter==4) {
            counter=0;
            
            Log.info("Brute Force Sync Death!! Counter=" + counter);
            if (status==READ_RESPONSE) {
                throw new IOException("Network Error: cannot read response!!");
            } else {
                throw new IOException("Network Error: cannot write request!!");
            }
            
        }
    }*/

    /**
     * Simulate immediate connection broken
     */
    /*private void forceBreakConnection() throws IOException {
        throw new IOException("Brute Force Connection Death!!");
    }*/
    
    /**
     * Add request properties for the configuration, profiles,
     * and locale of this system.
     * @param c current HttpConnection to receive user agent header
     */
    private void setConfig(HttpConnection c, int length) throws IOException {
        String ua = this.userAgent;

        String locale = System.getProperty(PROP_MICROEDITION_LOCALE);

        if (ua == null) {
            // Build the default user agent
            String conf = System.getProperty(PROP_MICROEDITION_CONFIGURATION);
            String prof = System.getProperty(PROP_MICROEDITION_PROFILES);

            ua = "Profile/" + prof + " Configuration/" + conf;
        }

        c.setRequestMethod(HttpConnection.POST);
        c.setRequestProperty("Connection", "Close");
        c.setRequestProperty(PROP_CONTENT_TYPE, "application/vnd.syncml+xml");


        c.setRequestProperty(PROP_CONTENT_LENGTH, String.valueOf(length));

        if (length == 0) {

            Log.error("content length has been set to 0 !");

        }

        // workaround to avoid errors on server
        // the client user agent must not be sent with User-Agent header
        c.setRequestProperty(PROP_USER_AGENT, ua);

        // we use Device-Agent for such an issue
        c.setRequestProperty(PROP_DEVICE_AGENT, createDeviceAgent());

        // If Set-Cookie header is set to empty value in http
        // server response, in Nokia S60 3ed. FP1 devices the Application crashes
        // It's due to a Symbian KVM bug.
        // A specific workaround has been implemented: the client sends a specific header
        // 'x-funambol-force-cookies' to force the server to set a 'Set-Cookie' header not empty.
        if (forceCookies) {
            c.setRequestProperty(PROP_FORCE_COOKIES, "true");
        }

        //Set Encoding and accepted properties: inflater or Gzip input Stream
        if (enableCompression) {
            c.setRequestProperty(PROP_ACCEPT_ENCODING, COMPRESSION_TYPE_GZIP);
            Log.debug("Encoding Response Required from Client: " + COMPRESSION_TYPE_GZIP);
        }

        if (this.sizeThreshold != 0) {
            c.setRequestProperty(PROP_SIZE_THRESHOLD,
                    String.valueOf(this.sizeThreshold));
        }

        if (locale != null) {
            c.setRequestProperty(PROP_CONTENT_LANGUAGE, locale);
        }

        // FIX to support jsessionid in cookies
        int jsIndex = requestURL.indexOf("jsessionid");
        if (jsIndex != -1) {
            String jsessionidString =
                    requestURL.substring(jsIndex, requestURL.length());
            int posEquals = jsessionidString.indexOf("=");
            String cookieName = jsessionidString.substring(0, posEquals);
            String cookieValue = jsessionidString.substring(posEquals+1);
            c.setRequestProperty("Cookie", cookieName.toUpperCase() + "=" + cookieValue);
        }
    }

  

    private void logHeaders(final HttpConnection c) throws IOException {

        // debug section for reading http response headers
        if (Log.getLogLevel() >= Log.INFO) {
            StringBuffer sbh = new StringBuffer();
            String tmp = "";
            for (int h = 0; h < 12; h++) {
                tmp = c.getHeaderFieldKey(h);
                sbh.append("[").append(h).append("] - ").
                        append(tmp);
                if ((tmp != null) && (!tmp.equals(""))) {
                    sbh.append(": ").append(c.getHeaderField(tmp)).append("\n");
                } else {
                    sbh.append("\n");
                }
            }
            Log.info("header: \n" + sbh.toString());
        }
    }

    private String createDeviceAgent() {
        StringBuffer sbAgent = new StringBuffer();
        sbAgent.append(System.getProperty("microedition.platform"));
        sbAgent.append(" ").append(System.getProperty("microedition.profiles"));
        sbAgent.append(" ").append(System.getProperty(
                "microedition.configuration"));
        return sbAgent.toString();
    }

    private void clear() {
        clearResponseStream();
        clearRequestStream();
        closeConnection();
    }

    private void clearRequestStream() {
        if (os != null) {
            try {
                os.close();
                os = null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.error("sendmessage: can't close output stream.");
            }
        }
    }

    private void clearResponseStream() {
        if (is != null) {
            try {
                is.close();
                is = null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.error("sendmessage: can't close input stream.");
            }
        }
    }

    private void closeConnection() {
        if (c != null) {
            try {
                c.close();
                c = null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                Log.error("sendmessage: can't close connection.");
            }
        }
    }

}

