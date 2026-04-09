package com.sdms.controller;

import com.sdms.model.Athlete;
import com.sdms.model.Coach;
import com.sdms.model.Official;
import com.sdms.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QRScanController {

    @FXML private TextField txtScan;
    @FXML private TextArea txtRaw;
    @FXML private Label lblStatus;
    @FXML private Label lblModule;
    @FXML private Label lblId;
    @FXML private Label lblName;
    @FXML private Label lblSchool;
    @FXML private Label lblExtra;

    @FXML public void initialize() {
        clearResult();
        txtScan.requestFocus();
    }

    @FXML private void onScan() {
        String raw = txtScan.getText() != null ? txtScan.getText().trim() : "";
        if (raw.isBlank()) {
            lblStatus.setText("Scan input is empty.");
            return;
        }

        txtRaw.setText(raw);

        try {
            ScanPayload payload = parsePayload(raw);
            if (payload.module == null || payload.id == null || payload.id.isBlank()) {
                lblStatus.setText("Could not parse QR payload. Expected Athlete Track format.");
                return;
            }

            switch (payload.module) {
                case "ATHLETE" -> showAthlete(payload.id);
                case "OFFICIAL" -> showOfficial(payload.id);
                case "COACH" -> showCoach(payload.id);
                default -> lblStatus.setText("Unsupported module in QR: " + payload.module);
            }
        } catch (Exception e) {
            lblStatus.setText("Scan error: " + e.getMessage());
        }
    }

    @FXML private void onClear() {
        txtScan.clear();
        txtRaw.clear();
        clearResult();
        txtScan.requestFocus();
    }

    @FXML private void onClose() {
        ((Stage) txtScan.getScene().getWindow()).close();
    }

    private void showAthlete(String athleteId) throws SQLException {
        Athlete a = DatabaseService.getInstance().findAthleteByAthleteId(athleteId);
        if (a == null) {
            lblStatus.setText("No athlete found for ID: " + athleteId);
            return;
        }
        lblModule.setText("Athlete");
        lblId.setText(a.getAthleteId());
        lblName.setText(a.getFullName());
        lblSchool.setText(nullSafe(a.getSchool()));
        lblExtra.setText("Sex: " + nullSafe(a.getSex()) + " | Contact: " + nullSafe(a.getContactNo()));
        lblStatus.setText("Match found.");
    }

    private void showOfficial(String officialId) throws SQLException {
        Official o = DatabaseService.getInstance().findOfficialByOfficialId(officialId);
        if (o == null) {
            lblStatus.setText("No official found for ID: " + officialId);
            return;
        }
        lblModule.setText("Official");
        lblId.setText(o.getOfficialId());
        lblName.setText(o.getFullName());
        lblSchool.setText(nullSafe(o.getSchool()));
        lblExtra.setText("Position: " + nullSafe(o.getCurrentPosition()) + " | Contact: " + nullSafe(o.getMobilePhone()));
        lblStatus.setText("Match found.");
    }

    private void showCoach(String coachId) throws SQLException {
        Coach c = DatabaseService.getInstance().findCoachByCoachId(coachId);
        if (c == null) {
            lblStatus.setText("No coach found for ID: " + coachId);
            return;
        }
        lblModule.setText("Coach");
        lblId.setText(c.getCoachId());
        lblName.setText(c.getFullName());
        lblSchool.setText(nullSafe(c.getSchool()));
        lblExtra.setText("Position: " + nullSafe(c.getCurrentPosition()) + " | Contact: " + nullSafe(c.getMobilePhone()));
        lblStatus.setText("Match found.");
    }

    private void clearResult() {
        lblStatus.setText("Ready. Scan a QR code and press Enter.");
        lblModule.setText("—");
        lblId.setText("—");
        lblName.setText("—");
        lblSchool.setText("—");
        lblExtra.setText("—");
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    private ScanPayload parsePayload(String raw) {
        String text = raw.trim();
        Map<String, String> map = new HashMap<>();
        String module = null;

        String[] parts = text.split("\\|");
        if (parts.length > 0 && parts[0].contains(":")) {
            String first = parts[0];
            module = first.substring(first.lastIndexOf(':') + 1).trim().toUpperCase();
        }

        for (String part : parts) {
            int idx = part.indexOf(':');
            if (idx > 0 && idx < part.length() - 1) {
                String key = part.substring(0, idx).trim().toUpperCase();
                String val = part.substring(idx + 1).trim();
                map.put(key, val);
            }
        }

        String id = map.get("ID");
        if (id == null || id.isBlank()) {
            id = extractStructuredId(text);
        }

        if (module == null || module.isBlank()) {
            module = inferModuleFromId(id);
        }

        return new ScanPayload(module, id);
    }

    private String extractStructuredId(String text) {
        Pattern p = Pattern.compile("(ATH-\\d{4}-\\d+|OFC-\\d{4}-\\d+|CCH-\\d{4}-\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find() ? m.group(1).toUpperCase() : null;
    }

    private String inferModuleFromId(String id) {
        if (id == null) return null;
        if (id.startsWith("ATH-")) return "ATHLETE";
        if (id.startsWith("OFC-")) return "OFFICIAL";
        if (id.startsWith("CCH-")) return "COACH";
        return null;
    }

    private record ScanPayload(String module, String id) {}
}
