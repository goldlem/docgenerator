package by.urbel.docgenerator.template.engine;

import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;

interface OnWordFoundCallback {

    void onWordFoundInRun(XWPFRun run, String bookmark);
    void onWordFoundInPreviousCurrentNextRun(List<XWPFRun> runs, int currentRun, String bookmark);
}
