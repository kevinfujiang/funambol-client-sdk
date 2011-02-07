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

package com.funambol.sapisync;

import com.funambol.sync.SyncAnchor;
import com.funambol.sync.SyncConfig;
import com.funambol.sync.SyncException;
import com.funambol.sync.SyncItem;
import com.funambol.sync.SyncSource;
import com.funambol.sync.SyncManagerI;
import com.funambol.util.Log;
import com.funambol.util.StringUtil;

/**
 * <code>SapiSyncManager</code> represents the synchronization engine performed
 * via SAPI.
 */
public class SapiSyncManager implements SyncManagerI {

    private static final String TAG_LOG = "SapiSyncManager";

    private SyncConfig syncConfig = null;
    private SapiSyncHandler sapiSyncHandler = null;
    
    public SapiSyncManager(SyncConfig config) {
        this.syncConfig = config;
        this.sapiSyncHandler = new SapiSyncHandler(
                StringUtil.extractAddressFromUrl(syncConfig.getSyncUrl()),
                syncConfig.getUserName(),
                syncConfig.getPassword());
    }

    /**
     * Synchronizes the given source, using the preferred sync mode defined for
     * that SyncSource.
     *
     * @param source the SyncSource to synchronize
     *
     * @throws SyncException If an error occurs during synchronization
     *
     */
    public void sync(SyncSource source) throws SyncException {
        sync(source, source.getSyncMode(), false);
    }

    public void sync(SyncSource source, boolean askServerDevInf) throws SyncException {
        sync(source, source.getSyncMode(), askServerDevInf);
    }

    public void sync(SyncSource src, int syncMode) {
        sync(src, syncMode, false);
    }

    /**
     * Synchronizes the given source, using the given sync mode.
     *
     * @param source the SyncSource to synchronize
     * @param syncMode the sync mode
     * @param askServerDevInf true if the engine shall ask for server caps
     *
     * @throws SyncException If an error occurs during synchronization
     */
    public synchronized void sync(SyncSource src, int syncMode, boolean askServerDevInf)
    throws SyncException {

        if (Log.isLoggable(Log.DEBUG)) {
            Log.debug(TAG_LOG, "Starting sync");
        }
        
        // TODO FIXME: check if resume is needed
        boolean resume = false;

        // TODO FIXME: twin detection
        
        performInitializationPhase(src, getActualSyncMode(src), resume);

        if(isDownloadPhaseNeeded(syncMode)) {
            performDownloadPhase(src, getActualDownloadSyncMode(src), resume);
        }

        if(isUploadPhaseNeeded(syncMode)) {
            performUploadPhase(src, getActualUploadSyncMode(src), resume);
        }
        
        performFinalizationPhase(src);
    }

    public void cancel() {
        // TODO FIXME
        performFinalizationPhase(null);
    }

    private void performInitializationPhase(SyncSource src, int syncMode,
            boolean resume) {
        src.beginSync(syncMode, resume);
        sapiSyncHandler.login();
    }

    private void performUploadPhase(SyncSource src, int syncMode, boolean resume) {

        boolean incremental = isIncrementalSync(syncMode);
        
        SyncItem item = getNextItemToUpload(src, incremental);
        while(item != null) {
            sapiSyncHandler.uploadItem(item);
            item = getNextItemToUpload(src, incremental);
        }
    }

    private SyncItem getNextItemToUpload(SyncSource src, boolean incremental) {
        if(incremental) {
            return src.getNextNewItem();
        } else {
            return src.getNextItem();
        }
    }

    private void performDownloadPhase(SyncSource src, int syncMode, boolean resume) {
        // TODO TBD
    }

    private void performFinalizationPhase(SyncSource src) {
        sapiSyncHandler.logout();
        if(src != null) {
            src.endSync();
        }
    }

    private int getActualSyncMode(SyncSource src) {
        SyncAnchor anchor = src.getSyncAnchor();
        if(anchor instanceof SapiSyncAnchor) {
            SapiSyncAnchor sapiAnchor = (SapiSyncAnchor)anchor;
            if((sapiAnchor.getDownloadAnchor() > 0) &&
               (sapiAnchor.getUploadAnchor() > 0)) {
                return SyncSource.INCREMENTAL_SYNC;
            } else {
                return SyncSource.FULL_SYNC;
            }
        } else {
            throw new SyncException(SyncException.ILLEGAL_ARGUMENT,
                    "Invalid source anchor format");
        }
    }

    private int getActualDownloadSyncMode(SyncSource src) {
        SyncAnchor anchor = src.getSyncAnchor();
        if(anchor instanceof SapiSyncAnchor) {
            SapiSyncAnchor sapiAnchor = (SapiSyncAnchor)anchor;
            if(sapiAnchor.getDownloadAnchor() > 0) {
                return SyncSource.INCREMENTAL_DOWNLOAD;
            } else {
                return SyncSource.FULL_DOWNLOAD;
            }
        } else {
            throw new SyncException(SyncException.ILLEGAL_ARGUMENT,
                    "Invalid source anchor format");
        }
    }

    private int getActualUploadSyncMode(SyncSource src) {
        SyncAnchor anchor = src.getSyncAnchor();
        if(anchor instanceof SapiSyncAnchor) {
            SapiSyncAnchor sapiAnchor = (SapiSyncAnchor)anchor;
            if(sapiAnchor.getUploadAnchor() > 0) {
                return SyncSource.INCREMENTAL_UPLOAD;
            } else {
                return SyncSource.FULL_UPLOAD;
            }
        } else {
            throw new SyncException(SyncException.ILLEGAL_ARGUMENT,
                    "Invalid source anchor format");
        }
    }

    private boolean isIncrementalSync(int syncMode) {
        return (syncMode == SyncSource.INCREMENTAL_SYNC) ||
               (syncMode == SyncSource.INCREMENTAL_DOWNLOAD) ||
               (syncMode == SyncSource.INCREMENTAL_UPLOAD);
    }

    private boolean isDownloadPhaseNeeded(int syncMode) {
        return (syncMode == SyncSource.FULL_SYNC) ||
               (syncMode == SyncSource.INCREMENTAL_SYNC) ||
               (syncMode == SyncSource.INCREMENTAL_DOWNLOAD);
    }

    private boolean isUploadPhaseNeeded(int syncMode) {
        return (syncMode == SyncSource.FULL_SYNC) ||
               (syncMode == SyncSource.INCREMENTAL_SYNC) ||
               (syncMode == SyncSource.INCREMENTAL_UPLOAD);
    }
}
