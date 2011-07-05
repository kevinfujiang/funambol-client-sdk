/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.client.controller;

import java.util.Vector;
import java.util.Enumeration;

import com.funambol.client.configuration.Configuration;
import com.funambol.client.customization.Customization;
import com.funambol.client.source.AppSyncSource;
import com.funambol.client.source.AppSyncSourceConfig;
import com.funambol.client.source.AppSyncSourceManager;
import com.funambol.client.ui.SettingsUIItem;
import com.funambol.client.ui.DevSettingsScreen;
import com.funambol.client.ui.DevSettingsUISyncSource;

import com.funambol.util.Log;

public class DevSettingsScreenController {

    private static final String TAG_LOG = "DevSettingsScreenController";

    private Vector miscSettingsUIItems = null;
    private Vector sourceSettingsUIItems = null;

    private AppSyncSourceManager appSyncSourceManager = null;

    private Customization customization;
    private Configuration configuration;

    private DevSettingsScreen screen;
    private Controller controller;

    public DevSettingsScreenController(Controller controller, DevSettingsScreen screen) {

        this.controller = controller;

        appSyncSourceManager = controller.getAppSyncSourceManager();
        
        this.customization = controller.getCustomization();
        this.configuration = controller.getConfiguration();
        this.screen = screen;
        controller.setDevSettingsScreenController(this);
    }

    public DevSettingsScreenController(Controller controller, Customization customization,
                                       Configuration configuration, AppSyncSourceManager appSyncSourceManager,
                                       DevSettingsScreen screen) {

        this.controller = controller;
        this.appSyncSourceManager = appSyncSourceManager;
        this.customization = customization;
        this.configuration = configuration;
        this.screen = screen;

        computeVisibleItems();
    }


    public Vector getVisibleSourceItems() {
        return sourceSettingsUIItems;
    }

    public void updateListOfSources() {
        sourceSettingsUIItems = null;
        screen.removeAllItems();
        computeVisibleItems();
        screen.layout();
    }

    public DevSettingsScreen getDevSettingsScreen() {
        return screen;
    }

    public void setDevSettingsScreen(DevSettingsScreen screen) {
        this.screen = screen;
    }

    private void computeVisibleItems() {

        if (Log.isLoggable(Log.TRACE)) {
            Log.trace(TAG_LOG, "Computing list of visible items");
        }

        // Check if visible items have already been computed
        if(sourceSettingsUIItems == null) {

            miscSettingsUIItems = new Vector();

            sourceSettingsUIItems = new Vector();

            Vector tempItems = new Vector();
            
            // Add an item for each registered source that has to fit into the 
            // screen.
            Enumeration sources = appSyncSourceManager.getRegisteredSources();
            while (sources.hasMoreElements()) {
                AppSyncSource appSource = (AppSyncSource)sources.nextElement();

                if (controller.isVisible(appSource) && appSource.hasSettings()) {
                    // Get the settings item for this source
                    DevSettingsUISyncSource item = appSource.createDevSettingsUISyncSource(screen);

                    if (item != null) {
                        item.setSource(appSource);

                        // Set the title
                        item.setTitle(appSource.getName());

                        // Load settings
                        item.loadSettings(configuration);

                        // The user can change this option if the source is
                        // working and active
                        if (!appSource.getConfig().getActive() || !appSource.isWorking()) {
                            item.setEnabled(false);
                        }
                        item.layout();

                        tempItems.addElement(item);
                    }
                }
            }

            sourceSettingsUIItems.setSize(tempItems.size());
            screen.setDevSettingsUISyncSourceCount(tempItems.size());

            // Now recompute the ui position for all available sources
            int sourcesOrder[] = customization.getSourcesOrder();
            int uiOrder = 0;
            for (int i=0;i<sourcesOrder.length;++i) {
                int sourceId = sourcesOrder[i];
                // If this is a working source, then set its UI position
                AppSyncSource source = appSyncSourceManager.getSource(sourceId);
                if (controller.isVisible(source)) {
                    if (Log.isLoggable(Log.DEBUG)) {
                        Log.debug(TAG_LOG, "Setting source " + source.getName() + " at position: " + uiOrder);
                    }
                    source.setUiSourceIndex(uiOrder++);
                }
            }

            for(int i=0; i<tempItems.size(); i++) {
                
                DevSettingsUISyncSource item = (DevSettingsUISyncSource)tempItems.elementAt(i);
                AppSyncSource source = item.getSource();

                if(source != null) {
                    int index = item.getSource().getUiSourceIndex();
                    sourceSettingsUIItems.setElementAt(item.getSource(), index);
                    screen.setDevSettingsUISyncSource(item, index);
                }
            }
            tempItems.removeAllElements();
        }
    }


    public boolean hasChanges() {

        boolean result = false;
        if(sourceSettingsUIItems != null) {
            Enumeration items = sourceSettingsUIItems.elements();
            while(items.hasMoreElements()) {
                AppSyncSource item = (AppSyncSource)items.nextElement();
                if(item.isWorking()) {
                    result |= item.getDevSettingsUISyncSource().hasChanges();
                }
            }
        }
        return result;
    }

    public void saveSettings() {

        if (Log.isLoggable(Log.DEBUG)) {
            Log.debug(TAG_LOG, "Saving sync settings");
        }

        if(sourceSettingsUIItems != null) {
            // Save SyncSources settings
            Enumeration items = sourceSettingsUIItems.elements();
            while(items.hasMoreElements()) {
                AppSyncSource item = (AppSyncSource)items.nextElement();
                if(item.isWorking()) {
                    DevSettingsUISyncSource settingsItem = item.getDevSettingsUISyncSource();
                    AppSyncSourceConfig config = item.getConfig();
                    // Save the settings
                    settingsItem.saveSettings(configuration);
                    config.commit();
                }
            }
        }
        // Save the global configuration
        configuration.save();

        updateListOfSources();
    }
}
