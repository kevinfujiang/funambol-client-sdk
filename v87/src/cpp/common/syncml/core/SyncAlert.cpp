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

#include "base/util/utils.h"
#include "syncml/core/AlertCode.h"
#include "syncml/core/SyncAlert.h"
#include "base/globalsdef.h"

USE_NAMESPACE

/*
 * This class represent a sync alert notified by the server.
 */

/*
 * Default Constructor
 */
SyncAlert::SyncAlert()
{
    syncType=0;
    contentType=0;
    serverURI=0;
}

/*
 * Destructor
 */
SyncAlert::~SyncAlert()
{
    if(serverURI)
        delete [] serverURI;
}

/*
 * Accessor methods
 */
int SyncAlert::getSyncType() { return syncType; }
int SyncAlert::getContentType () { return contentType; }
const char *SyncAlert::getServerURI () { return serverURI; }

/**
  @brief Set values for the object

  @param sync_type      Sync type (values: 6-10)
  @param content_type   MIME type
  @param len            Server URI lenght
  @param buf            Server URI characters

  @return
 */
int SyncAlert::set(int sync_type, int content_type, const char *uri)
{
    if (sync_type < 6 || sync_type > 10)
        return -1;
    syncType = sync_type+200;
    contentType = content_type;
    serverURI=stringdup(uri);
    return 0;
}

