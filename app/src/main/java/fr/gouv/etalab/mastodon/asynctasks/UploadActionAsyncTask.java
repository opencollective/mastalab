/* Copyright 2017 Thomas Schneider
 *
 * This file is a part of Mastodon Etalab for mastodon.etalab.gouv.fr
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Mastodon Etalab is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Thomas Schneider; if not,
 * see <http://www.gnu.org/licenses>. */
package fr.gouv.etalab.mastodon.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.io.InputStream;

import fr.gouv.etalab.mastodon.client.API;
import fr.gouv.etalab.mastodon.client.Entities.Attachment;
import fr.gouv.etalab.mastodon.interfaces.OnRetrieveAttachmentInterface;


/**
 * Created by Thomas on 01/05/2017.
 * Proceeds to file upload
 */

public class UploadActionAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private OnRetrieveAttachmentInterface listener;
    private Attachment attachment;
    private InputStream inputStream;
    private fr.gouv.etalab.mastodon.client.Entities.Status status;

    public UploadActionAsyncTask(Context context, InputStream inputStream, OnRetrieveAttachmentInterface onRetrieveAttachmentInterface){
        this.context = context;
        this.listener = onRetrieveAttachmentInterface;
        this.inputStream = inputStream;
    }

    @Override
    protected Void doInBackground(Void... params) {

        attachment = new API(context).uploadMedia(inputStream);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        listener.onRetrieveAttachment(attachment);
    }

}
