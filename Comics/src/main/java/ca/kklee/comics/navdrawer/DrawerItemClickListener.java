package ca.kklee.comics.navdrawer;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import ca.kklee.comics.HomeActivity;
import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 09/07/2014.
 */
public class DrawerItemClickListener implements OnItemClickListener {

    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private HomeActivity activity;

    public DrawerItemClickListener(HomeActivity activity, ViewPager viewPager, DrawerLayout drawerLayout) {
        this.viewPager = viewPager;
        this.drawerLayout = drawerLayout;
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i == ComicCollection.getInstance().getComics().length) {
        } else if (i < ComicCollection.getInstance().getComics().length) {
            viewPager.setCurrentItem(i, true);
            drawerLayout.closeDrawers();
        }
    }

}
