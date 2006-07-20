package com.fudco.jclass;

// Copyright 2004 Hewlett Packard, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Object representing an internalized Java .class file field descriptor.
 */
public class CF_field_info {

    private int myAccess_flags;
    private int myName_index;
    private int myDescriptor_index;
    private CF_attribute_info myAttributes[];

    /**
     * Constructor.
     *
     * @param access_flags     Flags bits denoting access permissions to and
     *                         properties of this field; values as defined in ClassFileConstants.
     * @param name_index       Index into the constant pool of the
     *                         CONSTANT_Utf8_info object containing the name of the field, stored
     *                         as a simple name (a Java identifier) or the special name "<init>".
     * @param descriptor_index Index into the constant pool of the
     *                         CONSTANT_Utf8_info object containing the field type signature.
     * @param attributes       Array of attribute objects describing this field's
     *                         attributes.
     */
    CF_field_info(int access_flags, int name_index, int descriptor_index,
                  CF_attribute_info attributes[]) {
        myAccess_flags = access_flags;
        myName_index = name_index;
        myDescriptor_index = descriptor_index;
        myAttributes = attributes;
    }

    /**
     * Obtain this field's access flags.
     *
     * @return a number whose bits denote access permissions and properties
     *         of this field.
     */
    public int access_flags() {
        return myAccess_flags;
    }

    /**
     * Obtain this field's name index.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the name of the field being described.
     */
    public int name_index() {
        return myName_index;
    }

    /**
     * Obtain this field's descriptor index.
     *
     * @return the index into the constant pool of the CONSTANT_Utf8_info
     *         object containing the field type signature.
     */
    public int descriptor_index() {
        return myDescriptor_index;
    }

    /**
     * Obtain the number of attributes this field has.
     *
     * @return the number of attributes for this field (this may be zero).
     */
    public int attributes_count() {
        return myAttributes.length;
    }

    /**
     * Obtain a descriptor for one of this field's attributes.
     *
     * @param index The index number of the attribute desired; this should be
     *              in the range 0 to attributes_count()-1.
     * @return a attribute_info object describing the indicated attribute.
     */
    public CF_attribute_info attributes(int index) {
        return myAttributes[index];
    }

    /**
     * Read and return a field_info object from a DataInputStream.
     *
     * @param in The DataInputStream to read from; this should be positioned
     *           at the first byte of the field descriptor being read.
     */
    static CF_field_info read(DataInputStream in)
      throws IOException {
        int access_flags = in.readUnsignedShort();
        int name_index = in.readUnsignedShort();
        int descriptor_index = in.readUnsignedShort();
        int attributes_count = in.readUnsignedShort();
        CF_attribute_info attributes[] =
          new CF_attribute_info[attributes_count];
        for (int i = 0; i < attributes_count; ++i) {
            attributes[i] = CF_attribute_info.read(in);
        }

        return new CF_field_info(access_flags, name_index, descriptor_index,
                                 attributes);
    }
}
