/*
 *  Copyright 2020-2023 CROZ d.o.o, the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.croz.nrich.excel.aot;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.zip.AsiExtraField;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.ResourceAlignmentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodeCommentExtraField;
import org.apache.commons.compress.archivers.zip.UnicodePathExtraField;
import org.apache.commons.compress.archivers.zip.X000A_NTFS;
import org.apache.commons.compress.archivers.zip.X0014_X509Certificates;
import org.apache.commons.compress.archivers.zip.X0015_CertificateIdForFile;
import org.apache.commons.compress.archivers.zip.X0016_CertificateIdForCentralDirectory;
import org.apache.commons.compress.archivers.zip.X0017_StrongEncryptionHeader;
import org.apache.commons.compress.archivers.zip.X0019_EncryptionRecipientCertificateList;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.X7875_NewUnix;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.SchemaType;
import org.openxmlformats.schemas.drawingml.x2006.main.impl.ThemeDocumentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl.CTPropertiesImpl;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.impl.PropertiesDocumentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.impl.STRelationshipIdImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.impl.STXstringImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTBorderImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTBordersImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTCellImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTCellStyleXfsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTCellXfsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTColImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTColorsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTColsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTDxfsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFillImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFillsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFontImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFontNameImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFontSizeImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTFontsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTIndexedColorsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTNumFmtImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTNumFmtsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTRgbColorImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTRowImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTRstImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTSheetDataImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTSheetDimensionImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTSheetImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTSheetsImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTSstImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTStylesheetImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTTableStylesImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTWorkbookImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTWorksheetImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.CTXfImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STBorderIdImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STCellRefImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STCellStyleXfIdImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STCellTypeImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STFillIdImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STFontIdImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STNumFmtIdImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STRefImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.STUnsignedIntHexImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.SstDocumentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.StyleSheetDocumentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.WorkbookDocumentImpl;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.impl.WorksheetDocumentImpl;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.List;
import java.util.ServiceLoader;

// these hints are because of Apache POI and should not be in nrich but to make usage a bit easier they are added here, if new versions of Apache POI register these hints they should be removed
public class ExcelRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    public static final String RESOURCE_PATTERN = "org/apache/poi/schemas/ooxml/*";

    public static final String RESOURCE_BUNDLE = "org.apache.xmlbeans.impl.regex.message";

    public static final List<Class<?>> CLASS_LIST = List.of(
        DefaultFlowMessageFactory.class, XSSFWorkbook.class, ParameterizedMessageFactory.class, AsiExtraField.class, JarMarker.class,
        ResourceAlignmentExtraField.class, UnicodeCommentExtraField.class, UnicodePathExtraField.class, X000A_NTFS.class, X0015_CertificateIdForFile.class,
        X0016_CertificateIdForCentralDirectory.class, X0017_StrongEncryptionHeader.class, X0019_EncryptionRecipientCertificateList.class,
        X5455_ExtendedTimestamp.class, X7875_NewUnix.class, X0014_X509Certificates.class, Zip64ExtendedInformationExtraField.class
    );

    public static final List<Class<?>> CONSTRUCTOR_CLASS_LIST = List.of(
        ThemeDocumentImpl.class, CTPropertiesImpl.class, PropertiesDocumentImpl.class, org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl.CTPropertiesImpl.class,
        org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl.PropertiesDocumentImpl.class, STRelationshipIdImpl.class, STXstringImpl.class, CTBorderImpl.class,
        CTBordersImpl.class, CTCellImpl.class, CTCellStyleXfsImpl.class, CTCellXfsImpl.class, CTColImpl.class, CTColorsImpl.class, CTColsImpl.class, CTDxfsImpl.class, CTFillImpl.class,
        CTFillsImpl.class, CTFontImpl.class, CTFontNameImpl.class, CTFontSizeImpl.class, CTFontsImpl.class, CTIndexedColorsImpl.class, CTNumFmtImpl.class, CTNumFmtsImpl.class,
        CTRgbColorImpl.class, CTRowImpl.class, CTRstImpl.class, CTSheetDataImpl.class, CTSheetDimensionImpl.class, CTSheetImpl.class, CTSheetsImpl.class, CTSstImpl.class, CTStylesheetImpl.class,
        CTTableStylesImpl.class, CTWorkbookImpl.class, CTWorksheetImpl.class, CTXfImpl.class, STBorderIdImpl.class, STCellRefImpl.class, STCellStyleXfIdImpl.class, STCellTypeImpl.class,
        STFillIdImpl.class, STFontIdImpl.class, STNumFmtIdImpl.class, STRefImpl.class, STUnsignedIntHexImpl.class, SstDocumentImpl.class, StyleSheetDocumentImpl.class,
        WorkbookDocumentImpl.class, WorksheetDocumentImpl.class
    );

    @SneakyThrows
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        CLASS_LIST.forEach(type -> registerHint(type, hints));
        CONSTRUCTOR_CLASS_LIST.forEach(type -> registerConstructor(type, hints));

        hints.resources().registerResourceBundle(RESOURCE_BUNDLE);
        hints.resources().registerPattern(RESOURCE_PATTERN);

        hints.reflection().registerMethod(ServiceLoader.class.getMethod("load", Class.class, ClassLoader.class), ExecutableMode.INVOKE);

        hints.reflection().registerField(STCellType.Enum.class.getField("table"));
    }

    @SneakyThrows
    private void registerHint(Class<?> type, RuntimeHints hints) {
        hints.reflection().registerType(type, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        hints.reflection().registerConstructor(type.getDeclaredConstructor(), ExecutableMode.INVOKE);
    }

    @SneakyThrows
    private void registerConstructor(Class<?> type, RuntimeHints hints) {
        hints.reflection().registerConstructor(type.getDeclaredConstructor(SchemaType.class), ExecutableMode.INVOKE);
    }
}
