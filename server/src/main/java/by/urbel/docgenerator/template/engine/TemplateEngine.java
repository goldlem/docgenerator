package by.urbel.docgenerator.template.engine;

import by.urbel.docgenerator.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.util.List;
import java.util.Map;

@Slf4j
public class TemplateEngine extends WordFinder {

    private static final int DEFAULT_TEXT_POS = 0;

    private Template template;
    private Map<String, String> mapping;

    public Template process(Template template) {
        this.template = template;
        this.mapping = template.mapping();
        replaceInTable();
        replaceInText();
        return template;
    }

    private void replaceInText() {
        findWordsInText(template.getDocument(), mapping.keySet());
    }

    private void replaceInTable() {
        findWordsInTable(template.getDocument(), mapping.keySet());
    }

    @Override
    public void onWordFoundInRun(XWPFRun run, String bookmark) {
        replaceWordInRun(run, bookmark);
    }

    @Override
    public void onWordFoundInPreviousCurrentNextRun(List<XWPFRun> runs, int currentRun, String bookmark) {
        replaceWordInPreviousCurrentNextRuns(runs, currentRun, bookmark);
    }

    private void replaceWordInPreviousCurrentNextRuns(List<XWPFRun> runs, int currentRun, String bookmark) {
        boolean replacedInPreviousRun = replaceRunTextStart(runs.get(currentRun - 1), bookmark);
        if (replacedInPreviousRun) {
            deleteTextFromRun(runs.get(currentRun));
        } else {
            replaceRunTextStart(runs.get(currentRun), bookmark);
        }
        cleanRunTextStart(runs.get(currentRun + 1), bookmark);
    }

    private void deleteTextFromRun(XWPFRun run) {
        run.setText("", DEFAULT_TEXT_POS);
    }

    private void replaceWordInRun(XWPFRun run, String bookmark) {
        String replacedText = run.getText(DEFAULT_TEXT_POS)
                .replace(bookmark, mapping.get(bookmark));
        run.setText(replacedText, DEFAULT_TEXT_POS);
    }

    private boolean replaceRunTextStart(XWPFRun run, String bookmark) {
        String text = run.getText(DEFAULT_TEXT_POS);
        String remainingBookmark = getRemainingBookmarkStart(text, bookmark);
        if (!remainingBookmark.isEmpty()) {
            text = text.replace(remainingBookmark, mapping.get(bookmark));
            run.setText(text, DEFAULT_TEXT_POS);
            return true;
        }
        return false;
    }

    private void cleanRunTextStart(XWPFRun run, String bookmark) {
        String text = run.getText(DEFAULT_TEXT_POS);
        String remainingBookmark = getRemainingBookmarkEnd(text, bookmark);
        text = text.replace(remainingBookmark, "");
        run.setText(text, DEFAULT_TEXT_POS);
    }

    private String getRemainingBookmarkEnd(String text, String bookmark) {
        if (!text.startsWith(bookmark)) {
            return getRemainingBookmarkEnd(text, bookmark.substring(1));
        } else {
            return bookmark;
        }
    }

    private String getRemainingBookmarkStart(String text, String bookmark) {
        if (!text.endsWith(bookmark)) {
            return getRemainingBookmarkStart(text, bookmark.substring(0, bookmark.length() - 1));
        } else {
            return bookmark;
        }
    }

}
