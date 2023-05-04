package eus.ehu.gleonis.gleonismastodonfx.utils;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachment;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Shape;

public class CachedImage {

    private final String blurHash;
    private final String url;
    private boolean blurHashed;

    private Image img;

    private Image blurHashedImg;

    public CachedImage(MediaAttachment mediaAttachment) {
        this.blurHash = mediaAttachment.getBlurhash();

        if (mediaAttachment.getRemote_url() != null && !mediaAttachment.getRemote_url().equals(""))
            this.url = mediaAttachment.getRemote_url();
        else
            this.url = mediaAttachment.getUrl();
    }

    public CachedImage(String blurHash, String url) {
        this.blurHash = blurHash;
        this.url = url;
    }

    /**
     * <p>
     * Will load the image in the background and set it to the ImageView.
     * This function is for image that have not blurhash.
     * </p>
     * If the image is a media attachment, use {@link #setBlurHashedImg(ImageView, int, int)} instead.
     *
     * @param view The ImageView to set the image to
     * @see #setBlurHashedImg(ImageView, int, int)
     */
    public void setImage(ImageView view) {
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);
        if (img != null)
            view.setImage(img);
        else {
            Utils.asyncTask(() -> Utils.loadImage(url), image -> {
                view.setImage(image);
                img = image;
                blurHashed = false;
            });
        }
    }

    public void setImage(Shape shape) {
        shape.setCache(true);
        shape.setCacheHint(CacheHint.SPEED);
        if (img != null)
            shape.setFill(new ImagePattern(img));
        else {
            Utils.asyncTask(() -> Utils.loadImage(url), image -> {
                shape.setFill(new ImagePattern(image));
                img = image;
                blurHashed = false;
            });
        }
    }

    public void setBlurHashedImg(ImageView view, int width, int height) {
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);
        if (img != null)
            view.setImage(img);
        else if (blurHash != null && !blurHash.isEmpty()) {
            Utils.asyncTask(() -> BHDecoder.INSTANCE.decode(blurHash, width, height, 1.0f, true), image -> {
                view.setImage(image);
                blurHashed = true;
                blurHashedImg = image;
            });
        }
    }

    // Will switch the image of an image view. It it's a blurhash image, the real one will be put/loaded in the background.
    // If it's a real image, the blurhashed one will be put/loaded in the background.
    public void switchImage(ImageView view, int width, int height) {
        if (blurHashed) {
            if (img != null) {
                view.setImage(img);
                blurHashed = false;
            } else {
                Utils.asyncTask(() -> Utils.loadImage(url), image -> {
                    view.setImage(image);
                    img = image;
                    blurHashed = false;
                });
            }
        } else {
            if (blurHashedImg != null) {
                view.setImage(blurHashedImg);
                blurHashed = true;
            } else if (blurHash != null && !blurHash.isEmpty()) {
                Utils.asyncTask(() -> BHDecoder.INSTANCE.decode(blurHash, width, height, 1.0f, true), image -> {
                    view.setImage(image);
                    blurHashed = true;
                    blurHashedImg = image;
                });
            }
        }
    }

}
