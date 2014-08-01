package ca.kklee.comics.navdrawer;

import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Created by Keith on 09/07/2014.
 */
public class DrawerItemClickListener implements OnItemClickListener {

    private ViewPager viewPager;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private Typeface normalFont, selectedFont;

    public DrawerItemClickListener(ViewPager viewPager, ListView drawerList, DrawerLayout drawerLayout) {
        this.viewPager = viewPager;
        this.drawerList = drawerList;
        this.drawerLayout = drawerLayout;
        normalFont = Typeface.createFromAsset(viewPager.getContext().getAssets(), "fonts/ComicNeue-Regular-Oblique.ttf");
        selectedFont = Typeface.createFromAsset(viewPager.getContext().getAssets(), "fonts/ComicNeue-Bold-Oblique.ttf");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i > 0) {
            viewPager.setCurrentItem(i - 1, true); //cause of header
            drawerLayout.closeDrawers();
//            setNavDrawerItemNormal();
//            ((TextView) view.findViewById(R.id.comic_title)).setTypeface(selectedFont);
        }
    }
//
//    public void setNavDrawerItemNormal()
//    {
//        for (int i=0; i< drawerList.getChildCount(); i++) {
//            View v = drawerList.getChildAt(i);
//            TextView textView = (TextView) v.findViewById(R.id.comic_title);
//            textView.setTypeface(normalFont);
//        }
//    }
}
