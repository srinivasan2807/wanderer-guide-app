package com.my.travel.wanderer.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FontUtils {
    public static Typeface sTypeface = null;
    public static Typeface sTypefaceThin = null;
    public static Typeface sTypefaceNomarl = null;
    public static Typeface sTypefaceBold = null;

    /**
     * Load font from filePath
     *
     * @param context
     * @param fileName
     * @return Typeface
     */
    public static Typeface loadFont(Context context, String fileName) {
        sTypeface = Typeface.createFromAsset(context.getAssets(), fileName);
        return sTypeface;
    }


    public static Typeface loadFontNormal(Context context, String fileName) {
        sTypefaceNomarl = Typeface.createFromAsset(context.getAssets(), fileName);
        return sTypefaceNomarl;
    }

    public static Typeface loadFontBold(Context context, String fileName) {
        sTypefaceBold = Typeface.createFromAsset(context.getAssets(), fileName);
        return sTypefaceBold;
    }

    public static Typeface loadFontThin(Context context, String fileName) {
        sTypefaceThin = Typeface.createFromAsset(context.getAssets(), fileName);
        return sTypefaceThin;
    }

    /**
     * Sets the font on all TextViews in the ViewGroup. Searches recursively for
     * all inner ViewGroups as well. Just add a check for any other views you
     * want to set as well (EditText, etc.)
     */
    public static void setFont(ViewGroup group) {
        int count = group.getChildCount();
        View v;

        if (group instanceof TextInputLayout) {
            TextInputLayout inputLayout = (TextInputLayout) group;
            inputLayout.setTypeface(sTypeface);
            inputLayout.getEditText().setTypeface(sTypeface);
        }

        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
                ((TextView) v).setTypeface(sTypeface);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v);
        }
    }

    /**
     * Sets the font on all TextViews in the ViewGroup. Searches recursively for
     * all inner ViewGroups as well. Just add a check for any other views you
     * want to set as well (EditText, etc.)
     */
    public static final int TYPE_LIGHT = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_BOLD = 2;
    public static final int TYPE_THIN = 3;

    public static void setFont(ViewGroup group, int type) {
        int count = group.getChildCount();
        View v;
        Typeface sTypefaces = sTypeface;
        if (type == TYPE_LIGHT) {
            sTypefaces = sTypeface;
        } else if (type == TYPE_NORMAL) {
            sTypefaces = sTypefaceNomarl;
        } else if (type == TYPE_BOLD) {
            sTypefaces = sTypefaceBold;
        } else if (type == TYPE_THIN) {
            sTypefaces = sTypefaceThin;
        }

        if (group instanceof TextInputLayout) {
            TextInputLayout inputLayout = (TextInputLayout) group;
            inputLayout.setTypeface(sTypefaces);
            inputLayout.getEditText().setTypeface(sTypefaces);
        }

        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button && sTypefaces != null) {
                ((TextView) v).setTypeface(sTypefaces);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v);
        }
    }


    /**
     * Set the font on TextInputLayout
     */
    public static void setFont(TextInputLayout inputLayout, int typeTextHelper, int typeEditText) {
        Typeface sTypefacesTextHelper = sTypeface;
        Typeface sTypefacesEditText = sTypeface;
        if (typeTextHelper == TYPE_LIGHT) {
            sTypefacesTextHelper = sTypeface;
        } else if (typeTextHelper == TYPE_NORMAL) {
            sTypefacesTextHelper = sTypefaceNomarl;
        } else if (typeTextHelper == TYPE_BOLD) {
            sTypefacesTextHelper = sTypefaceBold;
        } else if (typeTextHelper == TYPE_THIN) {
            sTypefacesTextHelper = sTypefaceThin;
        }
        if (typeEditText == TYPE_LIGHT) {
            sTypefacesEditText = sTypeface;
        } else if (typeTextHelper == TYPE_NORMAL) {
            sTypefacesEditText = sTypefaceNomarl;
        } else if (typeTextHelper == TYPE_BOLD) {
            sTypefacesEditText = sTypefaceBold;
        }else if (typeTextHelper == TYPE_THIN) {
            sTypefacesEditText = sTypefaceThin;
        }

        inputLayout.setTypeface(sTypefacesTextHelper);
        inputLayout.getEditText().setTypeface(sTypefacesEditText);
    }


    /**
     * Sets the font on TextView, TextInputLayout
     */
    public static void setFont(View v, int type) {
        Typeface sTypefaces = sTypeface;
        if (type == TYPE_LIGHT) {
            sTypefaces = sTypeface;
        } else if (type == TYPE_NORMAL) {
            sTypefaces = sTypefaceNomarl;
        } else if (type == TYPE_BOLD) {
            sTypefaces = sTypefaceBold;
        } else if (type == TYPE_THIN) {
            sTypefaces = sTypefaceThin;
        }

        if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
            ((TextView) v).setTypeface(sTypefaces);
        } else if (v instanceof TextInputLayout) {
            TextInputLayout inputLayout = (TextInputLayout) v;
            inputLayout.setTypeface(sTypefaces);
            inputLayout.getEditText().setTypeface(sTypefaces);
        }
    }

    public static void setFont(View v) {
        if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
            ((TextView) v).setTypeface(sTypeface);
        } else if (v instanceof TextInputLayout) {
            TextInputLayout inputLayout = (TextInputLayout) v;
            inputLayout.setTypeface(sTypeface);
            inputLayout.getEditText().setTypeface(sTypeface);
        }
    }

    /**
     * Load font from res/raw
     * <p/>
     * Font in Android Library - Stack Overflow
     * http://stackoverflow.com/questions/7610355/font-in-android-library
     *
     * @param context    Context
     * @param resourceId resourceId
     * @return Typeface or null
     */
    public static Typeface getTypefaceFromRaw(Context context, int resourceId) {
        InputStream inputStream = null;
        BufferedOutputStream bos = null;
        OutputStream os = null;
        Typeface typeface = null;
        try {
            // Load font(in res/raw) to memory
            inputStream = context.getResources().openRawResource(resourceId);

            // Output font to temporary file
            String fontFilePath = context.getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";

            os = new FileOutputStream(fontFilePath);
            bos = new BufferedOutputStream(os);

            byte[] buffer = new byte[inputStream.available()];
            int length = 0;
            while ((length = inputStream.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }

            // When loading completed, delete temporary files
            typeface = Typeface.createFromFile(fontFilePath);
            new File(fontFilePath).delete();
        } catch (Resources.NotFoundException e) {
            LoggerFactory.logStackTrace(e);
        } catch (IOException e) {
            LoggerFactory.logStackTrace(e);
        } finally {
            tryClose(bos);
            tryClose(os);
            tryClose(inputStream);
        }

        return typeface;
    }

    /**
     * Release closeable object
     *
     * @param obj closeable object
     */
    private static void tryClose(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                LoggerFactory.logStackTrace(e);
            }
        }
    }

}