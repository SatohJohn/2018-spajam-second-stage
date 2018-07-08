package john.example.jp.kotlinproject.utils;

import android.graphics.Bitmap;

import org.jcodec.common.model.Picture;

import static org.jcodec.common.model.ColorSpace.RGB;

public class CopyToPicture {
    public static Picture fromBitmap(Bitmap src) {
        Picture dst = Picture.create(src.getWidth(), src.getHeight(), RGB);
        fromBitmap(src, dst);
        return dst;
    }

    private static void fromBitmap(Bitmap src, Picture dst) {
        byte[] dstData = dst.getPlaneData(0);
        int[] packed = new int[src.getWidth() * src.getHeight()];

        src.getPixels(packed, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());

        for (int i = 0, srcOff = 0, dstOff = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++, srcOff++, dstOff += 3) {
                int rgb = packed[srcOff];
                dstData[dstOff]     = (byte) ((rgb >> 16) & 0xff);
                dstData[dstOff + 1] = (byte) ((rgb >> 8) & 0xff);
                dstData[dstOff + 2] = (byte) (rgb & 0xff);
            }
        }
    }
}
