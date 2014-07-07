package ca.kklee.comics.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.viewpager.ComicFragment;

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
        bundle.putInt("ID", position);

        ComicFragment comicFragment = new ComicFragment();
        comicFragment.setArguments(bundle);
        return comicFragment;
    }

    @Override
    public int getCount() {
        return ComicCollection.getInstance().getComics().length;
    }

    public CharSequence getPageTitle(int position) {
        return ComicCollection.getInstance().getComics()[position].getTitle();
    }
}
