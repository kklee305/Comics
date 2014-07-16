package ca.kklee.comics;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by Keith on 09/07/2014.
 */
public class DrawerItemClickListener implements OnItemClickListener {

    private ViewPager viewPager;
    private DrawerLayout drawerLayout;

    public DrawerItemClickListener(ViewPager viewPager, DrawerLayout drawerLayout) {
        this.viewPager = viewPager;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        viewPager.setCurrentItem(i, true);
        drawerLayout.closeDrawers();
    }
}
