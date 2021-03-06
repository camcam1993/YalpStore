package com.github.yeriomin.yalpstore.model;

import com.github.yeriomin.playstoreapi.AggregateRating;
import com.github.yeriomin.playstoreapi.AppDetails;
import com.github.yeriomin.playstoreapi.DocV2;
import com.github.yeriomin.playstoreapi.Image;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppBuilder {

    public static String suffixMil;
    public static String suffixBil;

    private static final int IMAGE_ICON = 4;
    private static final int IMAGE_SCREENSHOT = 1;

    static public App build(DocV2 details) {
        App app = new App();
        app.setDisplayName(details.getTitle());
        app.setDescription(details.getDescriptionHtml());
        app.setCategoryId(details.getRelatedLinks().getCategoryInfo().getAppCategory());
        if (details.getOfferCount() > 0) {
            app.setOfferType(details.getOffer(0).getOfferType());
            app.setFree(details.getOffer(0).getMicros() == 0);
        }
        fillAggregateRating(app, details.getAggregateRating());
        AppDetails appDetails = details.getDetails().getAppDetails();
        app.getPackageInfo().packageName = appDetails.getPackageName();
        app.setVersionName(appDetails.getVersionString());
        app.setVersionCode(appDetails.getVersionCode());
        app.setSize(appDetails.getInstallationSize());
        app.setInstalls(getInstallsNum(appDetails.getNumDownloads()));
        app.setUpdated(appDetails.getUploadDate());
        app.setChanges(appDetails.getRecentChangesHtml());
        app.setPermissions(appDetails.getPermissionList());
        fillImages(app, details.getImageList());
        fillDeveloper(app, appDetails);
        return app;
    }

    static private String getInstallsNum(String installsRaw) {
        Pattern pattern = Pattern.compile("[ ,>\\.\\+\\d\\s]+");
        Matcher matcher = pattern.matcher(installsRaw);
        if (matcher.find()) {
            return matcher.group(0)
                .replaceAll("[\\s\\.,]000[\\s\\.,]000[\\s\\.,]000", suffixBil)
                .replaceAll("[\\s\\.,]000[\\s\\.,]000", suffixMil)
                ;
        }
        return null;
    }

    static private void fillAggregateRating(App app, AggregateRating aggregateRating) {
        Rating rating = app.getRating();
        rating.setAverage(aggregateRating.getStarRating());
        rating.setStars(1, (int) aggregateRating.getOneStarRatings());
        rating.setStars(2, (int) aggregateRating.getTwoStarRatings());
        rating.setStars(3, (int) aggregateRating.getThreeStarRatings());
        rating.setStars(4, (int) aggregateRating.getFourStarRatings());
        rating.setStars(5, (int) aggregateRating.getFiveStarRatings());
    }

    static private void fillDeveloper(App app, AppDetails appDetails) {
        Developer developer = app.getDeveloper();
        developer.setName(appDetails.getDeveloperName());
        developer.setEmail(appDetails.getDeveloperEmail());
        developer.setWebsite(appDetails.getDeveloperWebsite());
    }

    static private void fillImages(App app, List<Image> images) {
        for (Image image: images) {
            if (image.getImageType() == IMAGE_ICON) {
                app.setIconUrl(image.getImageUrl());
            } else if (image.getImageType() == IMAGE_SCREENSHOT) {
                app.getScreenshotUrls().add(image.getImageUrl());
            }
        }
    }
}
