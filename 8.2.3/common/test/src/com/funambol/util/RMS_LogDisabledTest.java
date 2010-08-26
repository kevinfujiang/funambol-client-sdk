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

import com.funambol.util.LogViewer;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import junit.framework.*;

public class RMS_LogDisabledTest extends TestCase {

    private RMSAppender appender;

    /** Creates a new instance of RMS_LogDisabledTest */
    public RMS_LogDisabledTest(String name) {
        super(name);
    }

    public void setUp() {
        appender = new RMSAppender(RMSAppender.LOGDBNAME);
    }

    public void tearDown() {
        appender.closeLogFile();
    }

    public void testInitLogDISABLED() throws AssertionFailedError {
        Log.initLog(appender, Log.DISABLED);
        assertTrue(Log.getLogLevel() == Log.DISABLED);
        System.out.println("Log.getLogLevel(): " + Log.getLogLevel());
    }

    public void testNoLoggingSetLevel() throws AssertionFailedError {
        System.out.println("testNoLoggingSetLevel");
        
        Log.initLog(appender);
        System.out.println("log inited");
        Log.setLogLevel(Log.DISABLED);
        Log.error("Error");
        Log.info("Info");
        Log.debug("Debug");
        Log.trace("Trace");
        LogViewer lv = new LogViewer();
        String[] actualLog = lv.getLogEntries(lv.RMSLOG);
        assertTrue(actualLog[0].equals("No Log entries found."));
        System.out.println("===========================================[ OK ]");
    }
}