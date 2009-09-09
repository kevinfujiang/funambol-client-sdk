/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.common.pim;

import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import javax.microedition.pim.PIMException;

import com.funambol.util.Log;
import com.funambol.util.StringUtil;

public class Utils {

    /**
     * This function will add Category data to provided PIMItem.
     * 
     * @param field The categories field extracted from vCard
     * @param list This is the PIMList that the PIMItem belongs to.
     * @param item The PIMItem that will have Category data added to it.
     * @param CategoryTag The string which is SIF tag for Categories.
     */
    public void addCategories(String field, PIMList list, PIMItem item, boolean addNewCategory ) throws PIMException
    {
        int max = item.maxCategories();
        max = max < 0 ? Integer.MAX_VALUE : max;
        String sep[] = new String[2];
        sep[0] = "; ";
        sep[1] = ", ";
        final String[] categories = StringUtil.split(field, sep);
        
        if (categories.length > max){
            
            Log.info("Some categories being dropped (max = " + max + ", length = " + categories.length + ")");
        }
        for (int j = 0; j < (max < categories.length ? max : categories.length); j++){

            if (!list.isCategory(categories[j]) && addNewCategory){
                  list.addCategory(categories[j]);
            }
            
            if(list.isCategory(categories[j])){
                try {
                    item.addToCategory(categories[j]);
                }catch (PIMException e) {
                    Log.info("Exception while adding category ["+categories[j]+"]");
                }
            }
             
        }



        /** Fix for bug# 7275 to not have a null category
         *  following the steps:
         *  - Create a contact with a category on outlook and sync on the portal
         *  - Sync on BB
         *  - Opening contact, choose Categories and delete a cheched category
         *  - On outlook change the category and sync
         *  - Sync on BB; o contact you can see the new category and a "null" category
         */

        String[] finalCat = item.getCategories();
        for(int i=0 ; i<finalCat.length; i++){
            if(finalCat[i].equals("null")){
                item.removeFromCategory("null");
                list.deleteCategory("null", true);
            }
        }

    }
}


