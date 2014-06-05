package ca.kklee.comics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Keith on 02/06/2014.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("ID",position);
        ComicFragment cf = new ComicFragment();
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    public int getCount() {
        return ComicCollection.getInstance().getComics().length;
    }

    public CharSequence getPageTitle(int position) {
        return ComicCollection.getInstance().getComics()[position].getTitle();
    }
}
