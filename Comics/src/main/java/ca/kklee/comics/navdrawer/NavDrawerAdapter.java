package ca.kklee.comics.navdrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ca.kklee.comics.R;

/**
 * Created by Keith on 25/07/2014.
 */
public class NavDrawerAdapter extends ArrayAdapter<String> {

    private int resource;
    private String[] items;
    private LayoutInflater inflater;

    public NavDrawerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavItem holder;
        if (convertView == null) {
            convertView = inflater.inflate(resource,null);
            holder = new NavItem();
            holder.comicIcon = (ImageView) convertView.findViewById(R.id.comic_icon);
            holder.comicTitle = (TextView) convertView.findViewById(R.id.comic_title);
            holder.comicUpdate = (TextView) convertView.findViewById(R.id.comic_update);
            holder.newIcon = (ImageView) convertView.findViewById(R.id.new_icon);
            convertView.setTag(holder);
        } else {
            holder = (NavItem) convertView.getTag();
        }

        holder.comicTitle.setText(items[position]);

        return convertView;
    }

    static private class NavItem {
        ImageView comicIcon;
        TextView comicTitle;
        TextView comicUpdate;
        ImageView newIcon;
    }

}
