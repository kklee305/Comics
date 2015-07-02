package ca.kklee.comics.comic;

import org.jsoup.nodes.Document;

/**
 * Created by Keith on 11/06/2015.
 */
public class ComicDOMDictionary {

    protected static String getImageUrlFromDOM(Document dom, int id) {
        switch (ComicCollection.getInstance().getComics()[id].getFullTitle()) {
            case "Garfield":
                return dom.getElementById("home_comic").select("img[src]").attr("src");
            case "XKCD":
                return "http:" + dom.getElementById("comic").select("img[src]").attr("src");
            case "Nerf Now":
                return dom.getElementById("comic").select("img[src]").attr("src");
            case "Saturday Morning Breakfast Cereal":
                return ComicCollection.getInstance().getComics()[id].getUrl() + dom.getElementById("comicbody").select("img[src]").attr("src");
            case "Cyanide & Happiness":
                return "http:" + dom.getElementById("featured-comic").attr("src");
            case "MANvsMAGIC":
                return ComicCollection.getInstance().getComics()[id].getUrl() + dom.select("main").select("img[src]").attr("src");
            case "Dilbert":
                return dom.select("div[class*=img-comic-container").select("img[src]").attr("src");
            case "Extra Fabulous Comics":
                return dom.getElementById("comic").select("img[src]").attr("src");
            case "Penny Arcade":
                return dom.getElementById("comicFrame").select("img[src]").attr("src");
            case "Pigminted":
                return dom.select("figure[class*=photo-hires-item").select("img[src]").attr("src");
            case "CTRL+ALT+DEL":
                return dom.getElementById("content").select("img[src]").attr("src");
            case "Down the Upward Spiral":
                return dom.select("div[class*=wslide-slide-inner2").select("img[src]").attr("src");
            case "Peanuts":
            case "Calvin and Hobbes":
            case "2 Cows and a Chicken":
            case "Wizard of Id":
            case "Get Fuzzy":
            case "Dilbert Classics":
            case "Marmaduke":
                return dom.select("Body").select("img[src*=amuniversal]").attr("src");
            default:
                return "error";
        }
    }

}
