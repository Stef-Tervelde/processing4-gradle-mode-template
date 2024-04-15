package processing.mode.lines;

import processing.app.Base;
import processing.app.Mode;
import processing.app.ui.Editor;
import processing.app.ui.EditorException;
import processing.app.ui.EditorState;

import java.io.File;

public class LinesMode extends Mode{
    public LinesMode(Base base, File folder) {
        super(base, folder);
    }

    @Override
    public String getTitle() {
        return "Lines";
    }

    @Override
    public Editor createEditor(Base base, String path, EditorState state) throws EditorException {
        return null;
    }

    @Override
    public String getDefaultExtension() {
        return "pde";
    }

    @Override
    public String[] getExtensions() {
        return new String[]{ "pde" };
    }

    @Override
    public String[] getIgnorable() {
        return new String[0];
    }
}
