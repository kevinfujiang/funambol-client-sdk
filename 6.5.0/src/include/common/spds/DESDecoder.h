/*
 * Copyright (C) 2003-2007 Funambol, Inc
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY, TITLE, NONINFRINGEMENT or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA
 */
 #ifndef INCL_DES_DECODER
    #define INCL_DES_DECODER
/** @cond DEV */

    #include "spds/DataTransformer.h"

    #define DT_DES "des"

    struct DESDecInfo {
        long size;
        char* password;
    };

    class DESDecoder : public DataTransformer {
        public:

        DESDecoder();
        ~DESDecoder();

        /**
         * Decodes the input data with the DES algorithm. Note that no memory is
         * allocated for the transformed data. The input buffer will be
         * used for the decoded data.
         *
         * @param data the data to decode
         * @param info IN/OUT transformation info
         */
        char* transform(char* data, TransformationInfo& info);
    };
/** @endcond */
 #endif

