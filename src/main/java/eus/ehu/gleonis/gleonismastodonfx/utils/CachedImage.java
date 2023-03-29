package eus.ehu.gleonis.gleonismastodonfx.utils;

import eus.ehu.gleonis.gleonismastodonfx.utils.blurhash.BlurHash;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

public class CachedImage {

    private boolean blurHashedImg;

    private final String blurHash;

    private final String url;

    private Image img;

    public CachedImage(String blurHash, String url) {
        this.blurHash = blurHash;
        this.url = url;
    }

    /**
     * <p>
     * Will load the image in the background and set it to the ImageView.
     * This function is for image that have not blurhash.
     * </p>
     * If the image is a media attachment, use {@link #setImage(ImageView, int, int)} instead.
     *
     * @param view The ImageView to set the image to
     * @see #setImage(ImageView, int, int)
     */
    public void setImage(ImageView view) {
        if (img != null)
            view.setImage(img);
        else {
            Utils.asyncTask(() -> new Image(url), image -> {
                view.setImage(image);
                img = image;
                blurHashedImg = false;
            });
        }
    }

    public void setImage(Shape shape) {
        if (img != null)
            shape.setFill(new ImagePattern(img));
        else {
            Utils.asyncTask(() -> new Image(url), image -> {
                shape.setFill(new ImagePattern(image));
                img = image;
                blurHashedImg = false;
            });
        }
    }

    public void setImage(ImageView view, int width, int height) {
        if (img != null)
            view.setImage(img);
        else if (blurHash != null && !blurHash.isEmpty()) {
            img = BlurHash.decode(blurHash, width, height, 1.0f);
            blurHashedImg = true;
        } else if (!blurHashedImg) {
            Utils.asyncTask(() -> new Image(url), image -> {
                view.setImage(image);
                img = image;
                blurHashedImg = false;
            });
        }
    }

}
