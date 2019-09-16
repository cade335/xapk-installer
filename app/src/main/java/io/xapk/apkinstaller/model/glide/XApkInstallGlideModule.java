package io.xapk.apkinstaller.model.glide;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.ViewTarget;

import java.io.InputStream;

import io.xapk.apkinstaller.BuildConfig;
import io.xapk.apkinstaller.R;
import io.xapk.apkinstaller.model.bean.ApkIconUrl;
import io.xapk.apkinstaller.model.bean.AppedIconUrl;
import io.xapk.apkinstaller.utils.bean.xapk.XApkIconUrl;
import io.xapk.apkinstaller.model.glide.loader.ApkIconModelLoader;
import io.xapk.apkinstaller.model.glide.loader.AppedIconModelLoader;
import io.xapk.apkinstaller.model.glide.loader.XApkModelLoader;

@GlideModule
public class XApkInstallGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        if (!BuildConfig.IS_RELEASE) {
            builder.setLogLevel(Log.VERBOSE);
        }
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        ViewTarget.setTagId(R.id.glide_tag);
        registry
                .append(ApkIconUrl.class, InputStream.class, new ApkIconModelLoader.Factory())
                .append(AppedIconUrl.class, InputStream.class, new AppedIconModelLoader.Factory())
                .append(XApkIconUrl.class, InputStream.class, new XApkModelLoader.Factory());
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
