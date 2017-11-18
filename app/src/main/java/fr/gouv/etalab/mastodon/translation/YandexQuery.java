/* Copyright 2017 Thomas Schneider
 *
 * This file is a part of Mastalab
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Mastalab is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Mastalab; if not,
 * see <http://www.gnu.org/licenses>. */
package fr.gouv.etalab.mastodon.translation;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import fr.gouv.etalab.mastodon.client.Entities.Status;
import fr.gouv.etalab.mastodon.client.HttpsConnection;
import fr.gouv.etalab.mastodon.helper.Helper;
import fr.gouv.etalab.mastodon.interfaces.OnTranslatedInterface;

/**
 * Created by Thomas on 03/07/2017.
 * Yandex client API
 */
class YandexQuery {

    private OnTranslatedInterface listener;
    YandexQuery(OnTranslatedInterface listenner) {
        this.listener = listenner;
    }
    private static final String BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    private static final String YANDEX_KEY = "trnsl.1.1.20170703T074828Z.a95168c920f61b17.699437a40bbfbddc4cd57f345a75c83f0f30c420";

    void getYandexTextview(final Translate translate, final Status status, final String text, final String toLanguage) throws JSONException {
        if( text != null && text.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String str_response = new HttpsConnection().get(getAbsoluteUrl(text, toLanguage), 30, null, null );
                        listener.onTranslatedTextview(translate, status, str_response, false);
                    } catch (Exception e) {
                        listener.onTranslatedTextview(translate, status, null, true);
                    }
                }
            }).start();

        }else {
            listener.onTranslatedTextview(translate, status, "", false);
        }
    }
    void getYandexTranslation(final Translate translate, final Helper.targetField target, final String content, final String toLanguage) throws JSONException {
        if( content != null && content.length() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String str_response = new HttpsConnection().get(getAbsoluteUrl(content, toLanguage), 30, null, null );
                        listener.onTranslated(translate, target, str_response, false);
                    } catch (Exception e) {
                        listener.onTranslated(translate, target, "", false);
                    }
                }
            }).start();
        }else{
            listener.onTranslated(translate, target, "", false);
        }
    }

    private static String getAbsoluteUrl(String content, String toLanguage) {
        String key  = "key=" + YANDEX_KEY + "&";
        toLanguage = toLanguage.replace("null","");
        String lang = "lang=" + toLanguage + "&";
        String text;
        try {
            text = "text=" + URLEncoder.encode(content, "utf-8") + "&";
        } catch (UnsupportedEncodingException e) {
            text = "text=" + content + "&";
            e.printStackTrace();
        }
        String format = "format=html&";
        return BASE_URL + key + lang +format  + text ;
    }
}
