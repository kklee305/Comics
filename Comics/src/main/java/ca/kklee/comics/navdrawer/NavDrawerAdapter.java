package ca.kklee.comics.navdrawer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ca.kklee.comics.R;
import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 25/07/2014.
 */
public class NavDrawerAdapter extends ArrayAdapter<String> {

    private int resource;
    private String[] items;
    private LayoutInflater inflater;
    private Typeface type;
    private SharedPreferences prefForNew, prefForUpdate;

    public NavDrawerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        inflater = LayoutInflater.from(context);
        prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
        prefForUpdate = context.getSharedPreferences(SharedPrefConstants.COMICUPDATETIME, 0);
        type = Typeface.createFromAsset(context.getAssets(), "fonts/ComicNeue-Regular-Oblique.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavItem holder;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            holder = new NavItem();
            holder.comicIcon = (ImageView) convertView.findViewById(R.id.comic_icon);
            holder.comicTitle = (TextView) convertView.findViewById(R.id.comic_title);
            holder.comicTitle.setTypeface(type);
            holder.comicUpdate = (TextView) convertView.findViewById(R.id.comic_update);
            holder.newIcon = (ImageView) convertView.findViewById(R.id.new_icon);
            convertView.setTag(holder);
        } else {
            holder = (NavItem) convertView.getTag();
        }

        String title = ComicCollection.getInstance().getComics()[position].getTitle();

        holder.comicTitle.setText(items[position]);

        if (prefForNew.getBoolean(title, false)) {
            holder.newIcon.setVisibility(View.VISIBLE);
        } else {
            holder.newIcon.setVisibility(View.INVISIBLE);
        }

        calculateLastUpdate(title, holder.comicUpdate);

        return convertView;
    }

    private void calculateLastUpdate(String title, TextView comicUpdate) {
        long updateTime = prefForUpdate.getLong(title, 0);
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - updateTime;
        if (updateTime == 0) {
            comicUpdate.setText("--");
            return;
        }
        if (timeDiff < 1000 * 60) {
            comicUpdate.setText((int) (timeDiff / 1000) + "s");
        } else if (timeDiff < 1000 * 60 * 60) {
            comicUpdate.setText((int) (timeDiff / 60000) + "m");
        } else if (timeDiff < 1000 * 60 * 60 * 24) {
            comicUpdate.setText((int) (timeDiff / 3600000) + "hr");
        } else {
            comicUpdate.setText((int) (timeDiff / 86400000) + "d");
        }
    }

    static private class NavItem {
        ImageView comicIcon;
        TextView comicTitle;
        TextView comicUpdate;
        ImageView newIcon;
    }

}
