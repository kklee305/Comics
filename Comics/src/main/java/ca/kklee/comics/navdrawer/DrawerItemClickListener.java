package ca.kklee.comics.navdrawer;

import android.app.Dialog;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import ca.kklee.comics.HomeActivity;
import ca.kklee.comics.OptionsDialogFactory;
import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 09/07/2014.
 */
public class DrawerItemClickListener implements OnItemClickListener {

    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private HomeActivity activity;
    private ListView drawerList;

    public DrawerItemClickListener(HomeActivity activity, ViewPager viewPager, DrawerLayout drawerLayout, ListView drawerList) {
        this.viewPager = viewPager;
        this.drawerLayout = drawerLayout;
        this.activity = activity;
        this.drawerList = drawerList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i >= ComicCollection.getInstance().getComics().length){
            Dialog dialog = OptionsDialogFactory.createDialog(activity);
            dialog.show();
            drawerList.setItemChecked(viewPager.getCurrentItem(), true);
        } else {
            viewPager.setCurrentItem(i, true);
        }
        drawerLayout.closeDrawers();
    }

}
