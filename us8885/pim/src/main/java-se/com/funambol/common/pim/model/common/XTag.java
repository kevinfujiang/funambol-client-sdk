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

package com.funambol.common.pim.model.common;

/**
 * An object representing a custom property.
 */
public class XTag {

    public static final String HOME_XTAG   = "HomeXTag";
    public static final String WORK_XTAG   = "BusinessXTag";
    public static final String OTHER_XTAG  = "OtherXTag";
    
    //--------------------------------------------------------------- Properties

    private TypifiedPluralProperty xtag;
    private String xtagValue;

    /**
     * Returns the custom property.
     *
     * @return the underlying Property object
     */
    public TypifiedPluralProperty getXTag() {
       return xtag;
    }

    /**
     * Returns the tag for this custom property.
     *
     * @return the tag name
     */
    public String getXTagValue() {
       return xtagValue;
    }

    /**
     * Sets the tag name for this custom property
     *
     * @param xtagValue a tag name (including the "X-" part).
     */
    public void setXTagValue(String xtagValue) {
        this.xtagValue = xtagValue;
    }

    //------------------------------------------------------------- Constructors
    
    /**
     * Creates an empty custom property with no tag specified.
     */
    public XTag() {
        xtag = new TypifiedPluralProperty();
    }
}
