/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.test.resources;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleProvider;

public class MyResourcesProvider implements ResourceBundleProvider {
    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        String xmlName = toXMLName(baseName, locale);
        try (InputStream is = this.getClass().getModule().getResourceAsStream(xmlName)) {
            return new XMLResourceBundle(new BufferedInputStream(is));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String toXMLName(String baseName, Locale locale) {
        StringBuilder sb = new StringBuilder(baseName.replace('.', '/'));
        String lang = locale.getLanguage();
        if (!lang.isEmpty()) {
            sb.append('_').append(lang);
            String country = locale.getCountry();
            if (!country.isEmpty()) {
                sb.append('_').append(country);
            }
        }
        return sb.append(".xml").toString();
    }

    private static class XMLResourceBundle extends ResourceBundle {
        private Properties props;

        XMLResourceBundle(InputStream stream) throws IOException {
            props = new Properties();
            props.loadFromXML(stream);
        }

        @Override
        protected Object handleGetObject(String key) {
            if (key == null) {
                throw new NullPointerException();
            }
            return props.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            // Not implemented
            return null;
        }
    }
}