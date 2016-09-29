package biz.k11i.jackson;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

public interface Twitter {
    @Data
    class LongId {
        long id;
        String idStr;
    }

    @Data
    class Contributor {
        long id;
        String idStr;
        String screenName;
    }

    @Data
    class Coordinate {
        List<Double> coordinates;
        String type;
    }

    @Data
    class BoundingBox {
        List<List<List<Double>>> coordinates;
        String type;
    }

    @Data
    class Place {
        Map<String, Object> attributes;
        BoundingBox boundingBox;
        String country;
        String countryCode;
        String fullName;
        String id;
        String name;
        String placeType;
        String url;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode(callSuper = true)
    class User extends LongId {
        boolean contributorsEnabled;
        String createdAt;
        boolean defaultProfile;
        boolean defaultProfileImage;
        String description;
        Map<String, Object> entities;
        int favouritesCount;
        Boolean followRequestSent;
        Boolean following;
        int followersCount;
        int friendsCount;
        Boolean geoEnabled;
        Boolean isTranslator;
        String lang;
        int listedCount;
        String location;
        String name;
        Boolean notifications;
        String profileBackgroundColor;
        String profileBackgroundImageUrl;
        String profileBackgroundImageUrlHttps;
        String profileBackgroundTile;
        String profileBannerUrl;
        String profileImageUrl;
        String profileImageUrlHttps;
        String profileLinkColor;
        String profileSidebarBorderColor;
        String profileSidebarFillColor;
        String profileTextColor;
        boolean profileUseBackgroundImage;
        @Getter(AccessLevel.NONE)
        @Setter(AccessLevel.NONE)
        boolean _protected;
        String screenName;
        boolean showAllInlineMedia;
        Tweet status;
        int statusesCount;
        String timeZone;
        String url;
        Integer utcOffset;
        boolean verified;
        List<String> withheldInCountries;
        String withheldScope;

        public boolean isProtected() {
            return _protected;
        }
        public void setProtected(boolean value) {
            _protected = value;
        }
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode(callSuper = true)
    class Tweet extends LongId {
        List<Contributor> contributors;
        Coordinate coordinates;
        String createdAt;
        Long timestampMs;
        LongId currentUserRetweet;
        Map<String, Object> entities;
        Map<String, Object> extendedEntities;
        Integer favoriteCount;
        Boolean favorited;
        String filterLevel;
        Map geo;
        // id, idStr
        String inReplyToScreenName;
        Long inReplyToStatusId;
        String inReplyToStatusIdStr;
        Long inReplyToUserId;
        String inReplyToUserIdStr;
        String lang;
        Place place;
        Boolean possiblySensitive;
        Boolean isQuoteStatus;
        Tweet extendedTweet;
        Long quotedStatusId;
        String quotedStatusIdStr;
        Tweet quotedStatus;
        Map scopes;
        int retweetCount;
        boolean retweeted;
        Tweet retweetedStatus;
        String source;
        String text;
        String fullText;
        List<Integer> displayTextRange;
        boolean truncated;
        User user;
        Boolean withheldCopyright;
        List<String> withheldInCountries;
        String withheldScope;
    }
}
