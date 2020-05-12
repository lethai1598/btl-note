package com.example.btl_note.callbacks;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.btl_note.R;

public abstract class MainActionModelCallback implements ActionMode.Callback {
    ActionMode action;
    MenuItem countItem;
    MenuItem shareItem;
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.main_action_model, menu);
        this.action = actionMode;
        this.countItem = menu.findItem(R.id.action_checked_count);
        this.shareItem = menu.findItem(R.id.action_share_note);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    public void setCount(String checkedCount) {
        this.countItem.setTitle(checkedCount);
    }

    /**
     *
     * @param b :visible
     */
    public void changeShareItemVisible(boolean b){
            shareItem.setVisible(b);

    }

    public ActionMode getAction() {
        return action;
    }
}
