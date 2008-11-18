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

/**
 * This exception represents the base exception for coded error conditions.
 * It makes possible for the receiver to analize the error condition using
 * one exception class.
 * 
 * This class defines only the basic codes. Classes deriving this this can
 * define other codes.
 */
public class CodedException extends RuntimeException {

    /** Storage error. (Problem accessing the backend storage, read or write) */
    public static final int STORAGE_ERROR     = 10;
    /** Out of memory error. It's not used at the moment */
    public static final int MEMORY_ERROR      = 11;
    /** The limit (memory, items) in the client has been reached */
    public static final int LIMIT_ERROR       = 12;

    /** Another sync is in progress */
    public static final int CONCURRENCE_ERROR = 100;

    /** The code of the exception */
    private int code;

    /**
     * Constructs an instance of <code>CodedException</code> with thei
     * code and specified detail message.
     * @param code the error code
     * @param msg the detail message.
     */
    public CodedException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    /** Returns the code of this exception */
    public int getCode() {
        return code;
    }

}

