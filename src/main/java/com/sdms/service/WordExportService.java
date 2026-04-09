package com.sdms.service;

import com.sdms.model.Equipment;
import com.sdms.util.AppPaths;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class WordExportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final String FONT_BODY = "Bookman Old Style";
    private static final String FONT_HEADER_SCRIPT = "Old English Text MT";
    private static final String FONT_HEADER_SANS = "Tahoma";

    private WordExportService() {}

    public static String exportEquipmentMemorandum(Equipment e) throws Exception {
        Path exportDir = AppPaths.exportsDir();
        Files.createDirectories(exportDir);

        String safeId = safeFilePart(ns(e.getEquipmentId()));
        if (safeId.isBlank()) {
            safeId = "UNKNOWN";
        }

        Path output = exportDir.resolve("EQUIPMENT_MEMORANDUM_" + safeId + "_" + System.currentTimeMillis() + ".docx");

        try (XWPFDocument doc = new XWPFDocument(); OutputStream os = Files.newOutputStream(output)) {
            configureA4Page(doc);

            addHeaderSection(doc);

            addCenteredTitle(doc,
                    "MEMORANDUM RECEIPT FOR EQUIPMENT, SEMI-EXPANDABLE",
                    "AND NON-EXPANDABLE PROPERTY");

            addBorrowerInfoTable(doc, e);
            addItemsTable(doc, e.getItems());
            addAcknowledgement(doc);
            addIssuedByIssuedToTable(doc, e);
            addRemarksBlock(doc);
            addFooterBranding(doc);

            doc.write(os);
        }

        return output.toString();
    }

    private static void configureA4Page(XWPFDocument doc) {
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        CTPageSz pageSize = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
        pageSize.setW(BigInteger.valueOf(11906));
        pageSize.setH(BigInteger.valueOf(16838));
        pageSize.setOrient(STPageOrientation.PORTRAIT);

        CTPageMar margin = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        margin.setTop(BigInteger.valueOf(720));
        margin.setBottom(BigInteger.valueOf(720));
        margin.setLeft(BigInteger.valueOf(720));
        margin.setRight(BigInteger.valueOf(720));
    }

    private static void addHeaderSection(XWPFDocument doc) {
        addHeaderImage(doc, "/images/kagawaran-ng-edukasyon-logo.png", "kagawaran-ng-edukasyon-logo.png", 84, 72);

        XWPFParagraph text = doc.createParagraph();
        text.setAlignment(ParagraphAlignment.CENTER);
        text.setSpacingAfter(80);

        XWPFRun runScript = text.createRun();
        runScript.setFontFamily(FONT_HEADER_SCRIPT);
        runScript.setFontSize(11);
        runScript.setText("Republic of the Philippines");
        runScript.addBreak();
        runScript.setText("Department of Education");
        runScript.addBreak();

        XWPFRun runSans = text.createRun();
        runSans.setFontFamily(FONT_HEADER_SANS);
        runSans.setFontSize(10);
        runSans.setBold(false);
        runSans.setText("Region III-Central Luzon");
        runSans.addBreak();
        runSans.setText("SCHOOLS DIVISION OFFICE OF THE CITY OF BALIWAG");

        XWPFParagraph line = doc.createParagraph();
        line.setBorderBottom(Borders.THICK);
        line.setSpacingAfter(140);
    }

    private static void addHeaderImage(XWPFDocument doc, String resourcePath, String fileName,
                                       int widthPx, int heightPx) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingAfter(120);

        addImageRun(p, resourcePath, fileName, widthPx, heightPx);
    }

    private static void addCenteredTitle(XWPFDocument doc, String line1, String line2) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingAfter(160);

        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily(FONT_BODY);
        run.setFontSize(12);
        run.setText(line1);
        run.addBreak();
        run.setText(line2);
    }

    private static void addBorrowerInfoTable(XWPFDocument doc, Equipment e) {
        XWPFTable infoTable = doc.createTable(2, 3);
        infoTable.setWidth("100%");
        infoTable.setTableAlignment(TableRowAlign.CENTER);

        fillInfoCell(infoTable.getRow(0).getCell(0), "Name:", e.getBorrowerName());
        fillInfoCell(infoTable.getRow(0).getCell(1), "Designation:", e.getDesignation());
        fillInfoCell(infoTable.getRow(0).getCell(2), "School:", e.getSchool());

        fillInfoCell(infoTable.getRow(1).getCell(0), "Event:", e.getEvent());
        fillInfoCell(infoTable.getRow(1).getCell(1), "Mobile No:", e.getMobileNo());
        fillInfoCell(infoTable.getRow(1).getCell(2), "Date:", formatDate(e));

        setCellWidth(infoTable.getRow(0).getCell(0), 3400);
        setCellWidth(infoTable.getRow(0).getCell(1), 3400);
        setCellWidth(infoTable.getRow(0).getCell(2), 3700);

        XWPFParagraph spacer = doc.createParagraph();
        spacer.setSpacingAfter(120);
    }

    private static void fillInfoCell(XWPFTableCell cell, String label, String value) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setSpacingBefore(40);
        p.setSpacingAfter(40);

        XWPFRun labelRun = p.createRun();
        labelRun.setBold(true);
        labelRun.setFontFamily(FONT_BODY);
        labelRun.setFontSize(10);
        labelRun.setText(label + " ");

        XWPFRun valueRun = p.createRun();
        valueRun.setFontFamily(FONT_BODY);
        valueRun.setFontSize(10);
        valueRun.setText(ns(value));
    }

    private static void addItemsTable(XWPFDocument doc, List<Equipment.EquipmentItem> items) {
        XWPFTable table = doc.createTable(1, 6);
        table.setWidth("100%");

        String[] headers = {
                "QTY",
                "UNIT",
                "MAKE AND DESCRIPTION",
                "DATE BORROWED",
                "DATE RETURNED",
                "REMARKS"
        };

        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.removeParagraph(0);
            XWPFParagraph p = cell.addParagraph();
            p.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = p.createRun();
            run.setBold(true);
            run.setFontFamily(FONT_BODY);
            run.setFontSize(10);
            run.setText(headers[i]);
        }

        setCellWidth(headerRow.getCell(0), 900);
        setCellWidth(headerRow.getCell(1), 900);
        setCellWidth(headerRow.getCell(2), 3600);
        setCellWidth(headerRow.getCell(3), 1700);
        setCellWidth(headerRow.getCell(4), 1700);
        setCellWidth(headerRow.getCell(5), 1700);

        int minRows = 10;
        int usedRows = 0;

        if (items != null) {
            for (Equipment.EquipmentItem item : items) {
                XWPFTableRow row = table.createRow();
                writeItemCell(row.getCell(0), String.valueOf(item.qty), ParagraphAlignment.CENTER);
                writeItemCell(row.getCell(1), ns(item.unit), ParagraphAlignment.CENTER);
                writeItemCell(row.getCell(2), ns(item.makeAndDescription), ParagraphAlignment.LEFT);
                writeItemCell(row.getCell(3), item.dateBorrowed != null ? item.dateBorrowed.format(DATE_FMT) : "", ParagraphAlignment.CENTER);
                writeItemCell(row.getCell(4), item.dateReturned != null ? item.dateReturned.format(DATE_FMT) : "", ParagraphAlignment.CENTER);
                writeItemCell(row.getCell(5), ns(item.remarks), ParagraphAlignment.LEFT);
                usedRows++;
            }
        }

        while (usedRows < minRows) {
            XWPFTableRow row = table.createRow();
            for (int i = 0; i < 6; i++) {
                writeItemCell(row.getCell(i), "", i < 2 || i == 3 || i == 4
                        ? ParagraphAlignment.CENTER : ParagraphAlignment.LEFT);
            }
            usedRows++;
        }
    }

    private static void writeItemCell(XWPFTableCell cell, String text, ParagraphAlignment align) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(align);
        p.setSpacingBefore(30);
        p.setSpacingAfter(30);
        XWPFRun run = p.createRun();
        run.setFontFamily(FONT_BODY);
        run.setFontSize(10);
        run.setText(ns(text));
    }

    private static void addAcknowledgement(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(180);
        p.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun run = p.createRun();
        run.setFontFamily(FONT_BODY);
        run.setFontSize(10);
        run.setText("I acknowledge to have received this _____ day of _______________, all sports");
        run.addBreak();
        run.setText("equipment/supplies/item in good condition.");
    }

    private static void addIssuedByIssuedToTable(XWPFDocument doc, Equipment e) {
        XWPFTable signTable = doc.createTable(1, 2);
        signTable.setWidth("100%");

        XWPFTableCell issuedByCell = signTable.getRow(0).getCell(0);
        issuedByCell.removeParagraph(0);
        XWPFParagraph byLabel = issuedByCell.addParagraph();
        byLabel.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun byLabelRun = byLabel.createRun();
        byLabelRun.setBold(true);
        byLabelRun.setFontFamily(FONT_BODY);
        byLabelRun.setFontSize(10);
        byLabelRun.setText("ISSUED BY:");

        XWPFParagraph byName = issuedByCell.addParagraph();
        byName.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun byNameRun = byName.createRun();
        byNameRun.setFontFamily(FONT_BODY);
        byNameRun.setFontSize(10);
        byNameRun.setText("____________________________");

        XWPFParagraph byPrinted = issuedByCell.addParagraph();
        byPrinted.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun byPrintedRun = byPrinted.createRun();
        byPrintedRun.setFontFamily(FONT_BODY);
        byPrintedRun.setFontSize(9);
        byPrintedRun.setText("PRINTED NAME OVER SIGNATURE / DATE");

        if (!ns(e.getIssuedBy()).isBlank()) {
            XWPFParagraph byHint = issuedByCell.addParagraph();
            byHint.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun hintRun = byHint.createRun();
            hintRun.setItalic(true);
            hintRun.setFontFamily(FONT_BODY);
            hintRun.setFontSize(9);
            hintRun.setText("(" + ns(e.getIssuedBy()) + ")");
        }

        XWPFTableCell issuedToCell = signTable.getRow(0).getCell(1);
        issuedToCell.removeParagraph(0);
        XWPFParagraph toLabel = issuedToCell.addParagraph();
        toLabel.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun toLabelRun = toLabel.createRun();
        toLabelRun.setBold(true);
        toLabelRun.setFontFamily(FONT_BODY);
        toLabelRun.setFontSize(10);
        toLabelRun.setText("ISSUED TO:");

        XWPFParagraph toName = issuedToCell.addParagraph();
        toName.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun toNameRun = toName.createRun();
        toNameRun.setFontFamily(FONT_BODY);
        toNameRun.setFontSize(10);
        toNameRun.setText("____________________________");

        XWPFParagraph toPrinted = issuedToCell.addParagraph();
        toPrinted.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun toPrintedRun = toPrinted.createRun();
        toPrintedRun.setFontFamily(FONT_BODY);
        toPrintedRun.setFontSize(9);
        toPrintedRun.setText("PRINTED NAME OVER SIGNATURE / DATE");

        if (!ns(e.getIssuedTo()).isBlank()) {
            XWPFParagraph toHint = issuedToCell.addParagraph();
            toHint.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun hintRun = toHint.createRun();
            hintRun.setItalic(true);
            hintRun.setFontFamily(FONT_BODY);
            hintRun.setFontSize(9);
            hintRun.setText("(" + ns(e.getIssuedTo()) + ")");
        }
    }

    private static void addRemarksBlock(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(100);
        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setFontFamily(FONT_BODY);
        run.setFontSize(10);
        run.setText("REMARKS:");

        XWPFParagraph line1 = doc.createParagraph();
        line1.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun l1 = line1.createRun();
        l1.setFontFamily(FONT_BODY);
        l1.setFontSize(10);
        l1.setText("_______________________________________________________________");

        XWPFParagraph line2 = doc.createParagraph();
        line2.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun l2 = line2.createRun();
        l2.setFontFamily(FONT_BODY);
        l2.setFontSize(10);
        l2.setText("_______________________________________________________________");
    }

    private static void addFooterBranding(XWPFDocument doc) {
        XWPFParagraph topLine = doc.createParagraph();
        topLine.setSpacingBefore(180);
        topLine.setBorderTop(Borders.THICK);
        topLine.setSpacingAfter(70);

        XWPFTable footer = doc.createTable(1, 2);
        footer.setWidth("100%");
        footer.setTableAlignment(TableRowAlign.CENTER);
        footer.setTopBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        footer.setBottomBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        footer.setLeftBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        footer.setRightBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        footer.setInsideHBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");
        footer.setInsideVBorder(XWPFTable.XWPFBorderType.NONE, 0, 0, "FFFFFF");

        XWPFTableCell logosCell = footer.getRow(0).getCell(0);
        setCellWidth(logosCell, 3600);
        logosCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
        logosCell.removeParagraph(0);
        XWPFParagraph logos = logosCell.addParagraph();
        logos.setAlignment(ParagraphAlignment.LEFT);
        logos.setVerticalAlignment(TextAlignment.TOP);

        addImageRun(logos, "/images/DepED-logo.png", "DepED-logo.png", 58, 22);
        addSpacer(logos, " ");
        addImageRun(logos, "/images/bagong-pilipinas-logo.png", "bagong-pilipinas-logo.png", 58, 22);
        addSpacer(logos, " ");
        addImageRun(logos, "/images/SDO-Seal.png", "SDO-Seal.png", 28, 28);

        XWPFTableCell textCell = footer.getRow(0).getCell(1);
        setCellWidth(textCell, 7500);
        textCell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.TOP);
        textCell.removeParagraph(0);
        XWPFParagraph address = textCell.addParagraph();
        address.setAlignment(ParagraphAlignment.LEFT);
        address.setVerticalAlignment(TextAlignment.TOP);

        XWPFRun a = address.createRun();
        a.setFontFamily(FONT_BODY);
        a.setFontSize(9);
        a.setText("Address: Baliwag North District Compound, J. Buizon St. Poblacion, City of Baliwag, Bulacan");
        a.addBreak();
        a.setText("Contact Number: (044) 816-6041");
        a.addBreak();
        a.setText("Email Address: baliwag.city@deped.gov.ph");
    }

    private static void addImageRun(XWPFParagraph p, String resourcePath, String fileName,
                                    int widthPx, int heightPx) {
        XWPFRun run = p.createRun();
        try (InputStream is = WordExportService.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                return;
            }
            run.addPicture(is, Document.PICTURE_TYPE_PNG, fileName,
                    Units.toEMU(widthPx), Units.toEMU(heightPx));
        } catch (Exception ignored) {
            // Keep export generation resilient even when a branding image is invalid/missing.
        }
    }

    private static void addSpacer(XWPFParagraph p, String text) {
        XWPFRun spacer = p.createRun();
        spacer.setText(text);
    }

    private static void setCellWidth(XWPFTableCell cell, int widthTwips) {
        if (cell.getCTTc().getTcPr() == null) {
            cell.getCTTc().addNewTcPr();
        }
        if (!cell.getCTTc().getTcPr().isSetTcW()) {
            cell.getCTTc().getTcPr().addNewTcW();
        }
        cell.getCTTc().getTcPr().getTcW().setW(BigInteger.valueOf(widthTwips));
        cell.getCTTc().getTcPr().getTcW().setType(STTblWidth.DXA);
    }

    private static String formatDate(Equipment e) {
        return e.getDate() != null ? e.getDate().format(DATE_FMT) : "";
    }

    private static String safeFilePart(String v) {
        return v.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private static String ns(String s) {
        return s != null ? s : "";
    }
}
