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

package com.funambol.mail;

import com.funambol.storage.RmsRecordItem;
import com.funambol.util.StringUtil;

import java.util.Date;
import java.util.Enumeration;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Represents a mailbox folder on the device. Folders can contain
 * <code>Message</code> objects, other <code>Folder</code> objects or both.
 * This nesting capability enables a folder hierarchy for stored messages. The
 * different levels of hierarchy in a folder's full name are separated by the
 * hierarchy separator character <code>FOLDER_SEPARATOR</code>. You can retrieve
 * a contained folder by name using <code>getFolder()</code>, or a list of
 * contained folders by using <code>list()</code>
 */
public class Folder implements RmsRecordItem {

    public static final char FOLDER_ITEM_PREFIX = 'F';
    public static final char FOLDER_SEPARATOR = '/';
    public static final String ROOT_FOLDER_PATH = "/";
    
    // -------------------------------------------------------------- Attributes

    /**
     * The folder fullname (e.g. /Funambol/Inbox)
     */
    protected String fullname = null;

    /**
     * The folder role (e.g. Inbox)
     */
    protected String role = null;

    /**
     * The folder creation date
     */
    protected Date created = null;

    /**
     * Each <code>Folder</code> contains a reference to the <code>Store</code>
     */
    protected Store store = null;

    /**
     * The record id in the record store
     */
    protected int recordId;

    private final int UNDEFINED_RECORD_ID = -1;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructs a new <code>Folder</code> providing a reference to the 
     * <code>Store</code> in which it has to be created. It is used when
     * creating a Folder from a DataInputStream.
     * 
     * @param store The <code>Store</code> in which the folder has to be created
     */
    public Folder(Store store) {
        this(null, null, null, store);
    }

    /**
     * Constructs a new <code>Folder</code> providing a name for it, the role,
     * the creation date and a reference to the <code>Store</code> in which it
     * has to be created.
     *
     * @param fullname The fullname for this <code>Folder</code>
     * @param role The role for this <code>Folder</code>
     * @param created The creation date for this <code>Folder</code>
     * @param store The <code>Store</code> in which the folder has to be created
     */
    public Folder(String fullname, String role, Date created, Store store) {
        this.fullname = fullname;
        this.role = role;
        this.created = created;
        this.store = store;
        this.recordId = UNDEFINED_RECORD_ID;
    }

    /**
     * Returns the full name of this <code>Folder</code>.
     *
     * @return The string containing the full name of this <code>Folder</code>
     */
    public String getFullName() {
        return fullname;
    }

    /**
     * Returns the name of this <code>Folder</code>, taking the last part of
     * the fullname
     *
     * @return The string containing the name of this <code>Folder</code>
     */
    public String getName() {
        return fullname.substring(fullname.lastIndexOf(FOLDER_SEPARATOR)+1);
    }

    /**
     * Set the <code>Store</code> containing this <code>Folder</code>
     *
     * @param store A reference to the <code>Store</code> containing this
     * <code>Folder</code>
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Returns the <code>Folder</code> role
     *
     * @return The <code>Folder</code> role
     */
    public String getRole() {
        // Try to get the folder role from its name
        if(role == null) {
            String name = getName();
            if(StringUtil.equalsIgnoreCase(name, "Inbox")) {
                return "inbox";
            } else if(StringUtil.equalsIgnoreCase(name, "Outbox")) {
                return "outbox";
            } else if(StringUtil.equalsIgnoreCase(name, "Drafts")) {
                return "drafts";
            } else if(StringUtil.equalsIgnoreCase(name, "Sent")) {
                return "sent";
            }
        }
        return role;
    }

    /**
     * Set the <code>Folder</code> role
     * @param role The new role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the <code>Folder</code> creation date
     *
     * @return The <code>Folder</code> role
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Returns the parent Folder of this Folder
     *
     * @return The Folder object this Folder is child of
     */
    public Folder getParent() throws MailException {
        Folder parent = null;
        int idx = fullname.lastIndexOf(FOLDER_SEPARATOR);
        // To be a subfolder, the name must be at least "/a/b"
        if(idx > 0) {
            String parentName = fullname.substring(0, idx);
            parent = store.getFolder(parentName);
        } else if(idx == 0 && fullname.length()>1) {
            // The parent is the root folder
            parent = store.getFolder(Folder.ROOT_FOLDER_PATH);
        }
        return parent;
    }

    /**
     * Returns the delimiter character that separates this <code>Folder</code>'s
     * pathname from the names of immediate subfolders
     *
     * TODO: Perhaps it is better to define in a field the character used as
     * separator and not directly here (this adopted here is the solution by
     * Sun)
     *
     * @return The hierarchy separator character
     */
    public char getSeparator() {
        return FOLDER_SEPARATOR;
    }

    /**
     * This method returns the list of subfolders under this Folder
     *
     * @return The subfolders array
     * @throws com.funambol.mail.MailException
     */
    public Folder[] list() throws MailException {
        return store.list(fullname);
    }

    /**
     * Appends the given <code>Message</code> to this <code>Folder</code>
     *
     * @param msg The <code>Message</code> to be added
     * @throws com.funambol.mail.MailException
     */
    public void appendMessage(Message msg) throws MailException {
        msg.setParent(this);
        store.addChild(fullname, msg);
    }

    /**
     * Updates the given <code>Message</code>
     *
     * @param msg The <code>Message</code> to be updated
     * @throws com.funambol.mail.MailException
     */
    public void updateMessage(Message msg) throws MailException {
        store.updateChild(fullname, msg);
    }

    /**
     * Gets the <code>Message</code> object corresponding to the given record
     * ID.
     * 
     * @param recordId
     * @return
     * @throws com.funambol.mail.MailException
     */
    public Message getMessage(String recordId) throws MailException {
        Message ret = (Message)store.readChild(fullname, recordId);
        //if the message has been previously deleted this generates NPE
        if (ret!=null) {
            ret.setParent(this);
        }
        return ret;
    }

    /**
     * This method returns the <code>ChildrenEnumeration</code> of this
     * <code>Folder</code>.
     *
     * <b>NOTE</b>: the children could of different types: <code>Message</code>,
     * <code>Folder</code> or <code>AccountFolder</code>
     *
     * @return A <code>Enumeration</code> of the <code>Message</code> items
     * contained in this <code>Folder</code>
     *
     * @throws MailException If an error occurs accessing the Store
     */
    public ChildrenEnumeration getChildren() throws MailException {
        return store.getChildren(fullname);
    }

    /**
     * Deletes a <code>Message</code> from this <code>Folder</code>
     * The message is properly updated so that it is both complete (completely
     * loaded in memory) and its parent folder is cleared. After this call the
     * Message object is valid and can be used in any operation.
     * 
     * @param msg A reference to the message to be deleted
     */
    public void deleteMessage(Message msg) throws MailException {
        // Before removing the message we load it completely, so that we can
        // handle it without the need to read the store
        msg.getContent();
        // Now remove it from the store
        store.removeChild(fullname, Integer.toString(msg.getRecordId()));
    }

    /**
     * Deletes a <code>Message</code> from this <code>Folder</code>, using
     * the record id as key. If the client has one or more Message objects that
     * hold the message at this record id position, there is no guarantee these
     * objects are still valid after this call. For such cases the client should
     * use @see deleteMessage(Message)
     *
     * @param key A reference to the message to be deleted
     */
    public void deleteMessage(String recordId) throws MailException {
        store.removeChild(fullname, recordId);
    }

    /**
     * Get the total number of messages in this <code>Folder</code>
     */
    public int getMessageCount() throws MailException {
        return store.countChilds(fullname, Message.MESSAGE_ITEM_PREFIX);
    }

    //---------------------------------------------------------- Private methods

    /**
     * Check whether the provided prefix char represents a <code>Folder</code>
     * <code>DataInputStream</code>
     *
     * @param prefix The item prefix.
     * @return true If supported.
     */
    public static boolean isSupportedStream(char prefix) {
        return (prefix == Folder.FOLDER_ITEM_PREFIX);
    }

    // Append the prefix for a Folder record item
    protected void writeRecordPrefix(DataOutputStream dout) throws IOException {
        dout.writeChar(Folder.FOLDER_ITEM_PREFIX);
    }

    // -------------------------------------------- RmsRecordItem implementation

    /**
     * @see com.funambol.storage.Serializable#serialize(java.io.DataOutputStream) 
     */
    public void serialize(DataOutputStream dout) throws IOException {
        writeRecordPrefix(dout);
        dout.writeUTF(fullname);              // "/Funambol/Inbox"
        dout.writeUTF(role);                  // "Inbox"
        dout.writeLong(created.getTime());    // "1240907915"
    }

    /**
     * @see com.funambol.storage.Serializable#deserialize(java.io.DataInputStream)
     */
    public void deserialize(DataInputStream din) throws IOException {
        fullname = din.readUTF();
        role = din.readUTF();
        created = new Date(din.readLong());
    }

    /**
     * @see com.funambol.storage.RmsRecordItem#setRecordId(int)
     */
    public void setRecordId(int id) {
        recordId = id;
    }

    /**
     * @see com.funambol.storage.RmsRecordItem#getRecordId()
     */
    public int getRecordId() {
        return recordId;
    }
}

