package ca.kklee.comics.loaders;

import android.view.View;

import ca.kklee.comics.ComicCollection;

/**
 * Created by Keith on 28/06/2014.
 */
public class AbstractComicLoaderFactory {

    public static ComicLoader getLoader(View rootView, int id) {
        ComicLoader loader = null;
        switch (ComicCollection.getInstance().getComics()[id].getTitle()) {
            case "Garfield":
                loader = new GarfieldLoader(rootView, id);
                break;
            case "Calvin and Hobbes":
                loader = new CalvinHobbesLoader(rootView, id);
                break;
            case "XKCD":
                loader = new XKCDLoader(rootView, id);
                break;
            case "Nerf Now":
                loader = new NerfNowLoader(rootView, id);
                break;
            case "Peanuts":
                loader = new PeanutsLoader(rootView, id);
                break;
            case "Saturday Morning Breakfast Cereal":
                loader = new SMBCLoader(rootView, id);
                break;
            default:
                return null;
        }
        return loader;
    }
}
