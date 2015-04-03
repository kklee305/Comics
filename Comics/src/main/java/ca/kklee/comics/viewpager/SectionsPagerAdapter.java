package ca.kklee.comics.viewpager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 02/06/2014.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private SharedPreferences prefForNew;

    public SectionsPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        prefForNew = activity.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
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

    @Override
    public int getItemPosition(Object object) {
        ComicFragment fragment = (ComicFragment) object;
        int id = fragment.getArguments().getInt("ID");
        String title = ComicCollection.getInstance().getComics()[id].getTitle();
        if (prefForNew.getBoolean(title, false)) {
            ComicCollection.getInstance().getComics()[id].clearBitmap();
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }
}
