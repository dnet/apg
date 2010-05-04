/*
 * Copyright (C) 2010 Thialfihar <thi@thialfihar.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thialfihar.android.apg;

import org.openintents.intents.FileManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class FileDialog {
    private static EditText mFilename;
    private static ImageButton mBrowse;
    private static Activity mActivity;
    private static String mFileManagerTitle;
    private static String mFileManagerButton;
    private static int mRequestCode;

    public static interface OnClickListener {
        public void onCancelClick();
        public void onOkClick(String filename);
    }

    public static AlertDialog build(Activity activity, String title, String message,
                                    String defaultFile, OnClickListener onClickListener,
                                    String fileManagerTitle, String fileManagerButton,
                                    int requestCode) {
        LayoutInflater inflater =
            (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle(title);
        alert.setMessage(message);

        View view = (View) inflater.inflate(R.layout.file_dialog, null);

        mActivity = activity;
        mFilename = (EditText) view.findViewById(R.id.input);
        mFilename.setText(defaultFile);
        mBrowse = (ImageButton) view.findViewById(R.id.btn_browse);
        mBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
        mFileManagerTitle = fileManagerTitle;
        mFileManagerButton = fileManagerButton;
        mRequestCode = requestCode;

        alert.setView(view);

        final OnClickListener clickListener = onClickListener;

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clickListener.onOkClick(mFilename.getText().toString());
                                    }
                                });

        alert.setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        clickListener.onCancelClick();
                                    }
                                });
        return alert.create();
    }

    public static void setFilename(String filename) {
        if (mFilename != null) {
            mFilename.setText(filename);
        }
    }

    /**
     * Opens the file manager to select a file to open.
     */
    private static void openFile() {
        String filename = mFilename.getText().toString();

        Intent intent = new Intent(FileManager.ACTION_PICK_FILE);

        intent.setData(Uri.parse("file://" + filename));

        intent.putExtra(FileManager.EXTRA_TITLE, mFileManagerTitle);
        intent.putExtra(FileManager.EXTRA_BUTTON_TEXT, mFileManagerButton);

        try {
            mActivity.startActivityForResult(intent, mRequestCode);
        } catch (ActivityNotFoundException e) {
            // No compatible file manager was found.
            Toast.makeText(mActivity, R.string.no_filemanager_installed, Toast.LENGTH_SHORT).show();
        }
    }
}
