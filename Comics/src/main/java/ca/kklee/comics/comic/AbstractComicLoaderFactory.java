package ca.kklee.comics.comic;

import android.view.View;

import ca.kklee.comics.loaders.GarfieldLoader;
import ca.kklee.comics.loaders.GoComicsLoader;
import ca.kklee.comics.loaders.NerfNowLoader;
import ca.kklee.comics.loaders.PeanutsLoader;
import ca.kklee.comics.loaders.SMBCLoader;
import ca.kklee.comics.loaders.XKCDLoader;

/**
 * Created by Keith on 28/06/2014.
 */
public class AbstractComicLoaderFactory {

    public static ComicLoader getLoader(View rootView, int id) {
        ComicLoader loader;
        switch (ComicCollection.getInstance().getComics()[id].getTitle()) {
            case "Garfield":
                loader = new GarfieldLoader(rootView, id);
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
            case "Calvin and Hobbes":
            case "2 Cows and a Chicken":
            case "Wizard of Id":
            case "Get Fuzzy":
            case "Dilbert Classics":
            case "Marmaduke":
                loader = new GoComicsLoader(rootView, id);
                break;
            default:
                return null;
        }
        return loader;
    }
}
