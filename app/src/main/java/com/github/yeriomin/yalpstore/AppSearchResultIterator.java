package com.github.yeriomin.yalpstore;

import android.util.Log;

import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.SearchResponse;
import com.github.yeriomin.yalpstore.model.App;
import com.github.yeriomin.yalpstore.model.AppBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class AppSearchResultIterator implements Iterator<List<App>> {

    private GooglePlayAPI.SearchIterator iterator;
    private boolean hideNonfreeApps;
    private String categoryId = CategoryManager.TOP;

    public AppSearchResultIterator(GooglePlayAPI.SearchIterator iterator) {
        this.iterator = iterator;
    }

    public String getQuery() {
        return this.iterator.getQuery();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setHideNonfreeApps(boolean hideNonfreeApps) {
        this.hideNonfreeApps = hideNonfreeApps;
    }

    @Override
    public List<App> next() {
        List<App> apps = new ArrayList<>();
        SearchResponse response = iterator.next();
        for (DocV2 details : response.getDocList().get(0).getChildList()) {
            App app = AppBuilder.build(details);
            if (hideNonfreeApps && !app.isFree()) {
                Log.i(this.getClass().getName(), "Skipping non-free app " + app.getPackageName());
            } else {
                apps.add(app);
            }
        }
        return apps;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
