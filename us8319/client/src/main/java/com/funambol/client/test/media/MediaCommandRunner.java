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

package com.funambol.client.test.media;

import java.util.Vector;

import com.funambol.client.test.CommandRunner;
import com.funambol.client.test.basic.BasicUserCommands;


public class MediaCommandRunner extends CommandRunner implements MediaUserCommands {

    private static final String TAG_LOG = "MediaCommandRunner";

    public MediaCommandRunner(MediaRobot robot) {
        super(robot);
    }

    public boolean runCommand(String command, Vector pars) throws Throwable {
        if (ADD_PICTURE.equals(command)) {
            addMedia(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (ADD_VIDEO.equals(command)) {
            addMedia(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (ADD_FILE.equals(command)) {
            addMedia(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (ADD_PICTURE_ON_SERVER.equals(command)) {
            addMediaOnServer(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (ADD_VIDEO_ON_SERVER.equals(command)) {
            addMediaOnServer(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (ADD_FILE_ON_SERVER.equals(command)) {
            addMediaOnServer(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (DELETE_PICTURE.equals(command)) {
            deleteMedia(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (DELETE_VIDEO.equals(command)) {
            deleteMedia(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (DELETE_FILE.equals(command)) {
            deleteMedia(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (DELETE_PICTURE_ON_SERVER.equals(command)) {
            deleteMediaOnServer(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (DELETE_VIDEO_ON_SERVER.equals(command)) {
            deleteMediaOnServer(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (DELETE_FILE_ON_SERVER.equals(command)) {
            deleteMediaOnServer(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (DELETE_ALL_PICTURES.equals(command)) {
            deleteAllMedia(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (DELETE_ALL_VIDEOS.equals(command)) {
            deleteAllMedia(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (DELETE_ALL_FILES.equals(command)) {
            deleteAllMedia(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (DELETE_ALL_PICTURES_ON_SERVER.equals(command)) {
            deleteAllMediaOnServer(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (DELETE_ALL_VIDEOS_ON_SERVER.equals(command)) {
            deleteAllMediaOnServer(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (DELETE_ALL_FILES_ON_SERVER.equals(command)) {
            deleteAllMediaOnServer(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (FILL_LOCAL_STORAGE.equals(command)) {
            fillLocalStorage();
        } else if (RESTORE_LOCAL_STORAGE.equals(command)) {
            restoreLocalStorage();
        } else if (CHECK_PICTURES_COUNT.equals(command)) {
            checkMediaCount(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (CHECK_VIDEOS_COUNT.equals(command)) {
            checkMediaCount(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        }  else if (CHECK_FILES_COUNT.equals(command)) {
            checkMediaCount(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (CHECK_PICTURES_COUNT_ON_SERVER.equals(command)) {
            checkMediaCountOnServer(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (CHECK_VIDEOS_COUNT_ON_SERVER.equals(command)) {
            checkMediaCountOnServer(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        }  else if (CHECK_FILES_COUNT_ON_SERVER.equals(command)) {
            checkMediaCountOnServer(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (LEAVE_NO_FREE_SERVER_QUOTA_FOR_PICTURE.equals(command)) {
            leaveNoFreeServerQuota(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (LEAVE_NO_FREE_SERVER_QUOTA_FOR_VIDEO.equals(command)) {
            leaveNoFreeServerQuota(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (INTERRUPT_ITEM_UPLOAD.equals(command)) {
            interruptItem("sending", command, pars);
        } else if (INTERRUPT_ITEM_DOWNLOAD.equals(command)) {
            interruptItem("receiving", command, pars);
        } else if (OVERRIDE_PICTURE_CONTENT.equals(command)) {
            overrideMediaContent(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (OVERRIDE_VIDEO_CONTENT.equals(command)) {
            overrideMediaContent(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (OVERRIDE_FILE_CONTENT.equals(command)) {
            overrideMediaContent(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (OVERRIDE_PICTURE_CONTENT_ON_SERVER.equals(command)) {
            overrideMediaContentOnServer(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (OVERRIDE_VIDEO_CONTENT_ON_SERVER.equals(command)) {
            overrideMediaContentOnServer(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (OVERRIDE_FILE_CONTENT_ON_SERVER.equals(command)) {
            overrideMediaContentOnServer(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else if (CREATE_FILE.equals(command)) {
            createFile(command, pars);
        } else if (RENAME_FILE.equals(command)) {
            renameFile(command, pars);
        } else if (RENAME_FILE_ON_SERVER.equals(command)) {
            renameFileOnServer(command, pars);
        } else if (CHECK_PICTURE_CONTENT_INTEGRITY.equals(command)) {
            checkFileContentIntegrity(BasicUserCommands.SOURCE_NAME_PICTURES, command, pars);
        } else if (CHECK_VIDEO_CONTENT_INTEGRITY.equals(command)) {
            checkFileContentIntegrity(BasicUserCommands.SOURCE_NAME_VIDEOS, command, pars);
        } else if (CHECK_FILE_CONTENT_INTEGRITY.equals(command)) {
            checkFileContentIntegrity(BasicUserCommands.SOURCE_NAME_FILES, command, pars);
        } else {
            return false;
        }
        return true;
    }

    private MediaRobot getMediaRobot() {
        return (MediaRobot)robot;
    }

    private void addMedia(String type, String command, Vector args) throws Throwable {
        String filename = getParameter(args, 0);
        checkArgument(filename, "Missing filename in " + command);
        getMediaRobot().addMedia(type, filename);
    }

    private void addMediaOnServer(String type, String command, Vector args) throws Throwable {
        String filename = getParameter(args, 0);
        checkArgument(filename, "Missing filename in " + command);
        getMediaRobot().addMediaOnServer(type, filename);
    }

    private void deleteMedia(String type, String command, Vector args) throws Throwable {
        String filename = getParameter(args, 0);
        checkArgument(filename, "Missing filename in " + command);
        getMediaRobot().deleteMedia(type, filename);
    }
    
    private void deleteMediaOnServer(String type, String command, Vector args) throws Throwable {
        String filename = getParameter(args, 0);
        checkArgument(filename, "Missing filename in " + command);
        getMediaRobot().deleteMediaOnServer(type, filename);
    }

    private void deleteAllMedia(String type, String command, Vector args) throws Throwable {
        getMediaRobot().deleteAllMedia(type);
    }

    private void deleteAllMediaOnServer(String type, String command, Vector args) throws Throwable {
        getMediaRobot().deleteAllMediaOnServer(type);
    }

    private void checkMediaCount(String type, String command, Vector args) throws Throwable {
        String number = getParameter(args, 0);
        checkArgument(number, "Missing expected count in " + command);
        getMediaRobot().checkMediaCount(type, Integer.parseInt(number));
    }

    private void checkMediaCountOnServer(String type, String command, Vector args) throws Throwable {
        String number = getParameter(args, 0);
        checkArgument(number, "Missing expected count in " + command);
        getMediaRobot().checkMediaCountOnServer(type, Integer.parseInt(number));
    }

    private void interruptItem(String phase, String command, Vector args) throws Throwable {
        String name = getParameter(args, 0);
        String pos = getParameter(args, 1);
        String itemIdx = getParameter(args, 2);
        int p;
        if (pos != null) {
            p = Integer.parseInt(pos);
        } else {
            p = -1;
        }

        int idx;
        if (itemIdx != null) {
            idx = Integer.parseInt(itemIdx);
        } else {
            idx = -1;
        }
        getMediaRobot().interruptItem(phase, name, p, idx);
    }

    private void leaveNoFreeServerQuota(String type, String command, Vector args) throws Throwable {
        String filename = getParameter(args, 0);
        checkArgument(filename, "Missing filename in " + command);
        getMediaRobot().leaveNoFreeServerQuota(type, filename);
    }

    private void overrideMediaContent(String type, String command, Vector args) throws Throwable {
        String targetFileName = getParameter(args, 0);
        checkArgument(targetFileName, "Missing target filename in " + command);
        String sourceFileName = getParameter(args, 1);
        checkArgument(targetFileName, "Missing source filename in " + command);
        getMediaRobot().overrideMediaContent(type, targetFileName, sourceFileName);
    }

    private void overrideMediaContentOnServer(String type, String command, Vector args) throws Throwable {
        String targetFileName = getParameter(args, 0);
        checkArgument(targetFileName, "Missing target filename in " + command);
        String sourceFileName = getParameter(args, 1);
        checkArgument(targetFileName, "Missing source filename in " + command);
        getMediaRobot().overrideMediaContentOnServer(type, targetFileName, sourceFileName);
    }
    
    private void fillLocalStorage() {
        getMediaRobot().fillLocalStorage();
    }
    
    private void restoreLocalStorage() {
        getMediaRobot().restoreLocalStorage();
    }

    private void createFile(String command, Vector args) throws Throwable {
        String fileName = getParameter(args, 0);
        checkArgument(fileName, "Missing filename in " + command);
        String fileSizeString = getParameter(args, 1);
        checkArgument(fileName, "Missing file size in " + command);
        long fileSize = Long.parseLong(fileSizeString);
        getMediaRobot().createFile(fileName, fileSize);
    }

    private void renameFile(String command, Vector args) throws Throwable {
        String oldFileName = getParameter(args, 0);
        checkArgument(oldFileName, "Missing oldFileName in " + command);
        String newFileName = getParameter(args, 1);
        checkArgument(newFileName, "Missing newFileName in " + command);

        getMediaRobot().renameFile(oldFileName, newFileName);
    }

    private void renameFileOnServer(String command, Vector args) throws Throwable {
        String oldFileName = getParameter(args, 0);
        checkArgument(oldFileName, "Missing oldFileName in " + command);
        String newFileName = getParameter(args, 1);
        checkArgument(newFileName, "Missing newFileName in " + command);

        getMediaRobot().renameFileOnServer(oldFileName, newFileName);
    }

    private void checkFileContentIntegrity(String type, String command, Vector args) throws Throwable {
        String fileNameClient = getParameter(args, 0);
        String fileNameServer = getParameter(args, 1);
        checkArgument(fileNameClient, "Missing fileNameClient in " + command);
        if(fileNameServer == null) {
            fileNameServer = fileNameClient;
        }
        getMediaRobot().checkFileContentIntegrity(type, fileNameClient, fileNameServer);
    }
    
}

