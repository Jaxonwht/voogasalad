package authoringInterface.editor.memento;

import java.util.ArrayList;

public class EditorCaretaker {
    private ArrayList<EditorMemento> mementos = new ArrayList<>();

    // add memento to the list
    public void addMemento(EditorMemento m) {
        mementos.add(m);
    }

    // get the state from a certain point
    public EditorMemento getMemento(Integer index) {
        if (index < mementos.size()) {
            return mementos.get(index);
        }
        return mementos.get(mementos.size()-1);
    }
}