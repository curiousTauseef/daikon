// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.i18n;

import java.util.MissingResourceException;

/**
 * This class look for i18n <b>messages.properties</b> file named according to package of the class used when creating
 * an instance. Not only it looks for the <package_name>.message.properties file but if none found is looks for the
 * super classes package propeties too.
 */
public class ClassBasedI18nMessages extends I18nMessages {

    transient private Class<?> clazz;

    transient private String unknowKeyPrefix;

    /**
     * return the value associated to the key found in the bundle associated to the localeProvider and using the package
     * of the clazz to look for the message.properties file.
     *
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param clazz, clazz used to find the resource based on the package name.
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassBasedI18nMessages(LocaleProvider localeProvider, Class<?> clazz, String unknowKeyPrefix) {
        super(localeProvider);
        this.clazz = clazz;
        this.unknowKeyPrefix = unknowKeyPrefix == null ? "" : unknowKeyPrefix; //$NON-NLS-1$
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, Class<?>, String)} with unknowKeyPrefix set to null
     * 
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param clazz, clazz used to find the resource based on the package name.
     */
    public ClassBasedI18nMessages(LocaleProvider localeProvider, Class<?> clazz) {
        this(localeProvider, clazz, null);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, Class<?>, String)} with localeProvider set to null
     * 
     * @param clazz, clazz used to find the resource based on the package name.
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassBasedI18nMessages(Class<?> clazz, String unknowKeyPrefix) {
        this(null, clazz, unknowKeyPrefix);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(Class<?>, String)} with unknowKeyPrefix set to null
     * 
     * @param clazz, clazz used to find the resource based on the package name.
     */
    public ClassBasedI18nMessages(Class<?> clazz) {
        this(clazz, null);
    }

    @Override
    public String getMessage(String key, Object... arguments) {
        // get the ResouceBundle Value
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            String baseName = computeBaseName(currentClass);
            try {
                return getFormatedMessage(key, currentClass.getClassLoader(), baseName, arguments);
            } catch (MissingResourceException mre) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return unknowKeyPrefix + key;
    }

    /**
     * return a base name base the currentClass Package name and named messages.properties.
     * 
     * @param currentClass used to derive the package of the file
     * @return the base name base on the package name of the class.
     */
    private String computeBaseName(Class<?> currentClass) {
        return currentClass.getPackage().getName().concat(".messages"); //$NON-NLS-1$
    }

}
